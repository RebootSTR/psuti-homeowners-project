package ru.psuti.apache1337.homeowners.presentation.registration

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.psuti.apache1337.homeowners.domain.model.ResponseState
import ru.psuti.apache1337.homeowners.domain.registration.model.User
import ru.psuti.apache1337.homeowners.domain.registration.usecases.RegistrationOTPUseCase
import ru.psuti.apache1337.homeowners.domain.registration.usecases.RegistrationUseCase
import ru.psuti.apache1337.homeowners.domain.usecases.AutoLoginUseCase
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationOTPUseCase: RegistrationOTPUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val autoLoginUseCase: AutoLoginUseCase
) : ViewModel() {

    private var user: MutableLiveData<User?> = MutableLiveData(null)

    var code: LiveData<Int> = Transformations.switchMap(user) { user ->
        if (user == null) return@switchMap null
        else liveData {
            try {
                val code = registrationOTPUseCase.getOneTimeCode(user.phoneNumber)
                _registrationResponseState.postValue(ResponseState.Success)
                _isCodeShowed.value = false
                emit(code)
            } catch (e: IllegalArgumentException) {
                _registrationResponseState.postValue(ResponseState.Failure(e.message ?: ""))
            }

        }
    }
        get() {
            _isCodeShowed.value = true
            return field
        }

    private var _registrationResponseState: MutableLiveData<ResponseState> = MutableLiveData()
    val registrationResponseState: LiveData<ResponseState>
        get() = _registrationResponseState

    private val _logInResponseState: MutableLiveData<ResponseState> = MutableLiveData()
    val logInResponseState: LiveData<ResponseState>
        get() = _logInResponseState


    fun checkPhoneLength(phone: String) = phone.length == 18
    fun isUserChanged(user: User) = this.user.value != user
    fun checkCodeLength(code: String) = code.length == 5

    private var _isCodeShowed: MutableLiveData<Boolean> = MutableLiveData(true)
    val isCodeShowed: LiveData<Boolean>
        get() = _isCodeShowed

    fun sendCode(user: User) {
        this.user.value = user
        _registrationResponseState.value = ResponseState.Loading
    }

    fun checkCode(code: Int) {
        _logInResponseState.value = ResponseState.Loading
        viewModelScope.launch {
            try {
                val user = user.value!!
                registrationUseCase.registration(user, code)
                autoLoginUseCase.autoLogin(user.phoneNumber)
                _logInResponseState.postValue(ResponseState.Success)
            } catch (e: IllegalArgumentException) {
                _logInResponseState.postValue(ResponseState.Failure(e.message ?: ""))
            }
        }
    }

    private var _filledLastName: String? = null
    val filledLastName: String?
        get() = _filledLastName

    private var _filledFirstName: String? = null
    val filledFirstName: String?
        get() = _filledFirstName

    private var _filledMiddleName: String? = null
    val filledMiddleName: String?
        get() = _filledMiddleName

    private var _filledEmail: String? = null
    val filledEmail: String?
        get() = _filledEmail

    private var _filledCity: String? = null
    val filledCity: String?
        get() = _filledCity

    private var _filledStreet: String? = null
    val filledStreet: String?
        get() = _filledStreet

    private var _filledHouse: String? = null
    val filledHouse: String?
        get() = _filledHouse

    private var _filledKorpus: String? = null
    val filledKorpus: String?
        get() = _filledKorpus

    private var _filledApartment: String? = null
    val filledApartment: String?
        get() = _filledApartment


    private var _filledCode: String? = null
    val filledCode: String?
        get() = _filledCode

    fun rememberFields(
        lastName: String? = null,
        firstName: String? = null,
        middleName: String? = null,
        email: String? = null,
        city: String? = null,
        street: String? = null,
        house: String? = null,
        korpus: String? = null,
        apartment: String? = null,
        code: String? = null,
    ) {
        if (lastName != null)
            _filledLastName = lastName

        if (firstName != null)
            _filledFirstName = firstName

        if (middleName != null)
            _filledMiddleName = middleName

        if (email != null)
            _filledEmail = email

        if (city != null)
            _filledCity = city

        if (street != null)
            _filledStreet = street

        if (house != null)
            _filledHouse = house

        if (korpus != null)
            _filledKorpus = korpus

        if (apartment != null)
            _filledApartment = apartment

        if (code != null)
            _filledCode = code
    }

}