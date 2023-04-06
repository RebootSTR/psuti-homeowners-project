package ru.psuti.apache1337.homeowners.presentation.auth

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.psuti.apache1337.homeowners.domain.auth.usecases.AuthOTPUseCase
import ru.psuti.apache1337.homeowners.domain.auth.usecases.DemoUserUseCase
import ru.psuti.apache1337.homeowners.domain.auth.usecases.LogInUseCase
import ru.psuti.apache1337.homeowners.domain.model.ResponseState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authOTPUseCase: AuthOTPUseCase, private val logInUseCase: LogInUseCase, private val demoUserUseCase: DemoUserUseCase) :
    ViewModel() {


    private var phone: MutableLiveData<String> = MutableLiveData("")
    val code: LiveData<Int> = Transformations.switchMap(phone) { phone ->
        if (phone == "") return@switchMap null
        else liveData {
            try {
                val code = authOTPUseCase.getOneTimeCode(phone)
                _authResponseState.postValue(ResponseState.Success)
                _isCodeShowed.value = false
                emit(code)
            } catch (e: IllegalArgumentException) {
                _authResponseState.postValue(ResponseState.Failure(e.message ?: ""))
            }

        }
    }
        get() {
            _isCodeShowed.value = true
            return field
        }

    private val _authResponseState: MutableLiveData<ResponseState> = MutableLiveData()
    val authResponseState: LiveData<ResponseState>
        get() = _authResponseState

    private val _logInResponseState: MutableLiveData<ResponseState> = MutableLiveData()
    val logInResponseState: LiveData<ResponseState>
        get() = _logInResponseState

    private var _isCodeShowed: MutableLiveData<Boolean> = MutableLiveData(true)
    val isCodeShowed: LiveData<Boolean>
        get() = _isCodeShowed

    fun checkPhoneLength(phone: String) = phone.length == 18
    fun isPhoneChanged(phone: String) = this.phone.value != phone
    fun checkCodeLength(code: String) = code.length == 5


    fun sendCode(phone: String) {
        this.phone.value = phone
        _authResponseState.value = ResponseState.Loading
    }

    fun checkCode(code: Int) {
        _logInResponseState.value = ResponseState.Loading
        viewModelScope.launch {
            try {
                logInUseCase.logIn(phone.value!!, code)
                _logInResponseState.postValue(ResponseState.Success)
            } catch (e: IllegalArgumentException) {
                _logInResponseState.postValue(ResponseState.Failure(e.message ?: ""))
            }
        }
    }


    private var _filledCode: String? = null
    val filledCode: String?
        get() = _filledCode

    fun rememberFields(code: String? = null) {
        if (code != null)
            _filledCode = code
    }

    private val _demoUserResponseState: MutableLiveData<ResponseState> = MutableLiveData()
    val demoUserResponseState: LiveData<ResponseState>
        get() = _demoUserResponseState

    fun createDemoUser() {
        _demoUserResponseState.value = ResponseState.Loading
        viewModelScope.launch {
            demoUserUseCase.createDemoUser()
            _demoUserResponseState.value = ResponseState.Success
        }
    }
}