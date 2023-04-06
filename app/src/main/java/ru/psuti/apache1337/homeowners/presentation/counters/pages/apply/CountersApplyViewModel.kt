package ru.psuti.apache1337.homeowners.presentation.counters.pages.apply

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import ru.psuti.apache1337.homeowners.domain.counters.usecases.*
import ru.psuti.apache1337.homeowners.domain.usecases.AutoLoginUseCase
import javax.inject.Inject

@HiltViewModel
class CountersApplyViewModel @Inject constructor(
    private val getCountersUseCase: LoadCountersUseCase,
    private val sendCounterEntriesUseCase: SendCounterEntriesUseCase,
    private val cacheUnsentEntriesUseCase: CacheUnsentEntriesUseCase,
    private val sendCachedEntriesUseCase: SendCachedEntriesUseCase,
    private val getUserAddressUseCase: GetUserAddressUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val getDemoCountersUseCase: GetDemoCountersUseCase,
) : ViewModel() {

    private val _counterEntriesList = MutableLiveData(mutableListOf<CounterEntry>())
    val counterEntriesList: LiveData<MutableList<CounterEntry>>
        get() = _counterEntriesList

    private val _flagReRenderPrev = MutableLiveData<Boolean>(false)
    val flagReRenderPrev: LiveData<Boolean>
        get() = _flagReRenderPrev

    private val _flagClearCurr = MutableLiveData<Boolean>(false)
    val flagClearCurr: LiveData<Boolean>
        get() = _flagClearCurr

    private val _createCounterAlert = MutableLiveData<Boolean>(false)
    val createCounterAlert: LiveData<Boolean>
        get() = _createCounterAlert

    private val _isLoginNeeded = MutableLiveData<Boolean>(false)
    val isLoginNeeded: LiveData<Boolean>
        get() = _isLoginNeeded

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _countersList = MutableLiveData<MutableList<Counter>>()
    val countersList: LiveData<MutableList<Counter>>
        get() = _countersList

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    fun setLoginNeeded(v: Boolean) {
        _isLoginNeeded.value = v
    }

    fun setError(error: String) {
        _error.value = error
        _error.value = null
    }

    fun getCounters(isConnected: Boolean, demo: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!demo) {
                    try {
                        val res = getCountersUseCase.execute()

                        _countersList.postValue(res.toMutableList())
                    } catch (e: Exception) {
                        _error.postValue(e.message)
                    }
                } else {
                    val res = getDemoCountersUseCase.execute()
                    _countersList.postValue(res.toMutableList())
                }
            }
        }
        _createCounterAlert.value = false
    }

    fun clearCounters() {
        _countersList.value = null
    }

    fun cacheEntries() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheUnsentEntriesUseCase.execute(_counterEntriesList.value!!.toList())
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

    fun sendEntries(isConnected: Boolean, demo: Boolean) {
        if (_counterEntriesList.value != null && _counterEntriesList.value!!.isNotEmpty() && !demo) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    if (_counterEntriesList.value != null) {
                        if (isConnected) {
                            try {
                                sendCounterEntriesUseCase.execute(_counterEntriesList.value!!.toList())
                                _counterEntriesList.postValue(emptyList<CounterEntry>().toMutableList())
                            } catch (e: Exception) {
                                _error.postValue(e.message)
                            }
                        } else {
                            cacheEntries()
                        }
                    }
                }
            }
            for (item in _countersList.value!!) {
                item.alreadyUsed = false
            }
            setReRenderPrev()
            setClearCurr()
            getCounters(isConnected, false)
        }
    }

    fun addCounterEntry(entry: CounterEntry) {
        val prev = counterEntriesList.value
        prev?.add(entry)
        _counterEntriesList.postValue(prev)
        setReRenderPrev()
        Log.e("add", "add")
    }

    fun removeCounterEntry(id: String) {
        val prev = counterEntriesList.value
        val new = prev?.filter {
            it.id != id
        }
        _counterEntriesList.postValue(new?.toMutableList())
        setReRenderPrev()
    }

    fun editCounterEntry(newEntry: CounterEntry) {
        val prev = _counterEntriesList.value!!
        val index = prev.indexOfFirst {
            it.counter.name == newEntry.counter.name
        }
        prev[index] = newEntry
        _counterEntriesList.postValue(prev)

    }

    private fun setReRenderPrev() {
        _flagReRenderPrev.value = true
        _flagReRenderPrev.value = false
    }

    private fun setClearCurr() {
        _flagClearCurr.value = true
        _flagClearCurr.value = false
    }

    fun setCounterAlreadyUsed(name: String, value: Boolean) {
        _countersList.value?.find { it.name == name }?.alreadyUsed = value
        setReRenderPrev()
    }
}