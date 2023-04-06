package ru.psuti.apache1337.homeowners.presentation.splash

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.domain.model.ResponseState
import ru.psuti.apache1337.homeowners.domain.splash.usecases.BackendVersionUseCase
import ru.psuti.apache1337.homeowners.domain.usecases.AutoLoginUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appSharedPreferences: AppSharedPreferences,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val backendVersionUseCase: BackendVersionUseCase,
    private val sharedPreferences: AppSharedPreferences
) :
    ViewModel() {

    val backendVersion: LiveData<String> = liveData {
        val version = try {
            _isVersionReceived = true
            backendVersionUseCase.getBackendVersion()
        } catch (e: Exception) {
            _isVersionReceived = false
            "Error"
        }
        emit(version)
    }

    private val _autoLogInResponseState: MutableLiveData<ResponseState> = MutableLiveData()
    val logInResponseState: LiveData<ResponseState>
        get() = _autoLogInResponseState

    private var _isVersionReceived: Boolean = false
    val isVersionReceived: Boolean
        get() = _isVersionReceived


    fun autoLogIn() {
        _autoLogInResponseState.value = ResponseState.Loading
        viewModelScope.launch {
            delay(1000)
            val userId = appSharedPreferences.userId.get()
            val phone = appSharedPreferences.number.get()
            if (userId != 0 && phone != "") {
                try {
                    autoLoginUseCase.autoLogin(phone)
                    _autoLogInResponseState.value = ResponseState.Success
                } catch (e: Exception) {
                    _autoLogInResponseState.value = ResponseState.Failure("Offline mode")
                }
            } else {
                _autoLogInResponseState.value = ResponseState.Failure("No user saved")
            }
        }
    }

    fun saveBackendVersion(versionText: String) {
        sharedPreferences.backendVersion.set(versionText)
    }
}