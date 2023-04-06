package ru.psuti.apache1337.homeowners.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.psuti.apache1337.homeowners.data.registration.RegistrationRepositoryImpl
import ru.psuti.apache1337.homeowners.data.registration.net.RegistrationApiService
import ru.psuti.apache1337.homeowners.data.AppDatabase
import ru.psuti.apache1337.homeowners.domain.registration.RegistrationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RegistrationModule {
    @Provides
    @Singleton
    fun provideRegistrationApiService(retrofit: Retrofit): RegistrationApiService {
        return retrofit.create(RegistrationApiService::class.java)
    }


    @Module
    @InstallIn(SingletonComponent::class)
    interface RegistrationBinds {
        @Binds
        @Singleton
        fun bindRegistrationRepository(impl: RegistrationRepositoryImpl): RegistrationRepository
    }
}