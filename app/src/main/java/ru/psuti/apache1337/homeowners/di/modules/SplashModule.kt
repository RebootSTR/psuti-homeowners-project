package ru.psuti.apache1337.homeowners.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.psuti.apache1337.homeowners.data.splash.SplashRepositoryImpl
import ru.psuti.apache1337.homeowners.data.splash.net.SplashApiService
import ru.psuti.apache1337.homeowners.domain.splash.SplashRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SplashModule {
    @Provides
    @Singleton
    fun provideSplashApiService(retrofit: Retrofit): SplashApiService {
        return retrofit.create(SplashApiService::class.java)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface SplashBinds {
        @Binds
        @Singleton
        fun bindSplashRepository(impl: SplashRepositoryImpl): SplashRepository
    }

}