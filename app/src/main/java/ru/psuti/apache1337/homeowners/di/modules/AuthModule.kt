package ru.psuti.apache1337.homeowners.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.psuti.apache1337.homeowners.data.AppDatabase
import ru.psuti.apache1337.homeowners.data.auth.AuthRepositoryImpl
import ru.psuti.apache1337.homeowners.data.auth.db.AuthDao
import ru.psuti.apache1337.homeowners.data.auth.net.AuthApiService
import ru.psuti.apache1337.homeowners.domain.auth.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthDao(appDatabase: AppDatabase): AuthDao {
        return appDatabase.authDao()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface AuthBinds {
        @Binds
        @Singleton
        fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    }
}

