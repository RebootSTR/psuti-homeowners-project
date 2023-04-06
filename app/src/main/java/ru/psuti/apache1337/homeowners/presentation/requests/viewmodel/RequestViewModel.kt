package ru.psuti.apache1337.homeowners.presentation.requests.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.domain.profile.Blocker
import ru.psuti.apache1337.homeowners.domain.profile.usecases.GetUserDataUseCase
import ru.psuti.apache1337.homeowners.domain.requests.model.DateRanges
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestStatus
import ru.psuti.apache1337.homeowners.domain.requests.usecases.*
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    private val getHistoryRequestsUseCase: GetHistoryRequestsUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val sendRequestUseCase: SendRequestUseCase,
    private val editRequestUseCase: EditRequestUseCase,
    private val getFileUseCase: GetFileUseCase,
    private val sendFileUseCase: SendFileUseCase
) : ViewModel() {

    val sendButtonBlocker = Blocker()
    val profile = getUserDataUseCase.execute()

    val date: MutableLiveData<String> = MutableLiveData<String>("")
    val status: MutableLiveData<String> = MutableLiveData<String>("")
    private val _data = getHistoryRequestsUseCase.execute()
    private val _filterTrigger: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val filteredData = MediatorLiveData<List<RequestModel>>().apply {
        value = emptyList()
        val action = { date: String, status: String, list: List<RequestModel> ->
            value = getSorted(date, status, list)
        }
        addSource(_filterTrigger) {
            if (!it) return@addSource
            action(date.value ?: "", status.value ?: "", _data.value ?: emptyList())
        }
        addSource(_data) {
            action(date.value ?: "", status.value ?: "", it)
        }
    }

    fun filterRequests() {
        _filterTrigger.value = true
    }

    fun sendRequest(theme: String, text: String, fileName: String): LiveData<Response> {
        val request = RequestModel(
            theme = theme,
            request = text,
            date = LocalDateTime.now(),
            status = RequestStatus.NEW,
            fileName = fileName
        )
        return liveData(viewModelScope.coroutineContext) {
            emit(sendRequestUseCase.execute(request))
        }
    }

    fun getSorted(date: String, status: String, list: List<RequestModel>): List<RequestModel> {
        var results: List<RequestModel> = ArrayList<RequestModel>().apply {
            addAll(list)
        }

        when (date) {
            DateRanges.LAST_MONTH.text -> results = results.filter {
                val now = LocalDateTime.now()
                val firstDayOfMonth = now.minusDays(30)
                return@filter firstDayOfMonth <= it.date
            }

            DateRanges.LAST_YEAR.text -> results = results.filter {
                val now = LocalDateTime.now()
                val firstDayOfYear = now.minusDays(365)
                return@filter firstDayOfYear <= it.date
            }

            DateRanges.LAST_QUARTER.text -> results = results.filter {
                val now = LocalDateTime.now()
                val firstDayOfQuarter = now.minusDays(91)
                return@filter firstDayOfQuarter <= it.date
            }
        }

        when (status) {
            RequestStatus.ACCEPTED.text -> results = results.filter {
                it.status == RequestStatus.ACCEPTED
            }

            RequestStatus.CANCELED.text -> results = results.filter {
                it.status == RequestStatus.CANCELED
            }

            RequestStatus.NEW.text -> results = results.filter {
                it.status == RequestStatus.NEW
            }

            RequestStatus.COMPLETE.text -> results = results.filter {
                it.status == RequestStatus.COMPLETE
            }
        }
        return results
    }

    fun loadFile(file: File, fileName: String): LiveData<Response> {
        return liveData(viewModelScope.coroutineContext) {
            emit(sendFileUseCase.execute(file, fileName))
        }
    }

    fun saveFile(fileName: String): LiveData<Response> {
        return liveData(viewModelScope.coroutineContext) {
            emit(getFileUseCase.execute(fileName))
        }
    }

    fun editRequest(theme: String, text: String, fileName: String, id: Int): LiveData<Response> {
        val request = RequestModel(
            id = id,
            theme = theme,
            request = text,
            date = LocalDateTime.now(),
            status = RequestStatus.NEW,
            fileName = fileName
        )
        return liveData(viewModelScope.coroutineContext) {
            emit(editRequestUseCase.execute(request))
        }
    }
}