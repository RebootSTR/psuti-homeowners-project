package ru.psuti.apache1337.homeowners.presentation.counters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.psuti.apache1337.homeowners.domain.counters.usecases.GetUserIdUseCase
import javax.inject.Inject

@HiltViewModel
class CountersViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {
    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int>
        get() = _id

    fun getId(): Int {
        return getUserIdUseCase.execute()
    }
}