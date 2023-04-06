package ru.psuti.apache1337.homeowners.presentation.counters.pages.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterFilter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import ru.psuti.apache1337.homeowners.domain.counters.usecases.FilterHistoryUseCase
import ru.psuti.apache1337.homeowners.domain.counters.usecases.GetDemoHistoryUseCase
import ru.psuti.apache1337.homeowners.domain.counters.usecases.GetUserAddressUseCase
import ru.psuti.apache1337.homeowners.domain.counters.usecases.LoadCountersHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class CountersHistoryViewModel @Inject constructor(
    private val getCountersHistoryUseCase: LoadCountersHistoryUseCase,
    private val getUserAddressUseCase: GetUserAddressUseCase,
    private val filterHistoryUseCase: FilterHistoryUseCase,
    private val getDemoHistoryUseCase: GetDemoHistoryUseCase
) : ViewModel() {

    private val _filter = MutableLiveData<CounterFilter>()
    val filter: LiveData<CounterFilter>
        get() = _filter

    private val _data = MutableLiveData<List<CounterHistoryEntry>>()
    val data: LiveData<List<CounterHistoryEntry>>
        get() = _data

    private val _sorted = MutableLiveData<List<CounterHistoryEntry>>()
    val sorted: LiveData<List<CounterHistoryEntry>>
        get() = _sorted

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    fun updateFilter(filter: CounterFilter) {
        _filter.postValue(filter)
    }

    fun getCountersHistory(isConnected: Boolean, demo: Boolean) {
        Log.e("called", "get")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!demo) {
                    val res = getCountersHistoryUseCase.execute()
                    Log.e("vm", res.toString())
                    _sorted.postValue(res)
                } else {
                    val res = getDemoHistoryUseCase.execute()
                    _sorted.postValue(res)
                }
            }
        }
    }

    fun getAddress(isConnected: Boolean, demo: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!demo) {
                    val res = getUserAddressUseCase.execute()
                    Log.e("d", res)
                    _address.postValue(res)
                } else {
                    _address.postValue("г. Москва ул. Красная Пресня д. 44 к/стр. 1 кв. 42")
                }
            }
        }
    }

    fun sort(isConnected: Boolean, demo: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sorted = filterHistoryUseCase.execute(filter.value!!, demo)
                _sorted.postValue(sorted)
            }
        }


    }

}