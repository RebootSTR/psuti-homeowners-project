package ru.psuti.apache1337.homeowners.presentation.profile.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.domain.profile.Blocker
import ru.psuti.apache1337.homeowners.domain.profile.model.ProfileModel
import ru.psuti.apache1337.homeowners.domain.profile.usecases.GetUserDataUseCase
import ru.psuti.apache1337.homeowners.domain.profile.usecases.LogoutUseCase
import ru.psuti.apache1337.homeowners.domain.profile.usecases.UpdateProfileUseCse
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateProfileUseCse: UpdateProfileUseCse,
    private val logoutUseCase: LogoutUseCase,
    private val appSharedPreferences: AppSharedPreferences
) : ViewModel() {

    val buttonBlocker = Blocker()
    val profile = getUserDataUseCase.execute()

    val backendVersion = appSharedPreferences.backendVersion.get()


    val isLogoutAccepted = MutableLiveData<Boolean>(false)
    val isLogout = MutableLiveData<Response>(null)

    fun save(profile: ProfileModel): LiveData<Response> {
        return liveData(context = viewModelScope.coroutineContext) {
            emit(updateProfileUseCse.execute(profile))
        }
    }

    fun acceptLogout() {
        isLogoutAccepted.value = true
    }

    fun logout() {
        viewModelScope.launch {
            isLogout.postValue(logoutUseCase.execute())
        }
    }

}