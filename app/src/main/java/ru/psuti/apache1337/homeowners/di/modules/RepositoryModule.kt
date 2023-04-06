package ru.psuti.apache1337.homeowners.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.psuti.apache1337.homeowners.data.profile.repository.ProfileRepositoryImpl
import ru.psuti.apache1337.homeowners.domain.profile.repository.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideProfileRepository(rep: ProfileRepositoryImpl): ProfileRepository

}