package ru.psuti.apache1337.homeowners.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.psuti.apache1337.homeowners.data.AppDatabase
import ru.psuti.apache1337.homeowners.data.counters.CountersMapper
import ru.psuti.apache1337.homeowners.data.counters.CountersRepositoryImpl
import ru.psuti.apache1337.homeowners.data.counters.db.CountersDao
import ru.psuti.apache1337.homeowners.data.counters.net.CountersApiService
import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CountersModule {

    @Provides
    @Singleton
    fun provideCountersApiService(retrofit: Retrofit): CountersApiService {
        return retrofit.create(CountersApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCountersDao(db: AppDatabase): CountersDao {
        return db.countersDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CountersBindings {
    @Binds
    abstract fun bindCountersRepository(repo: CountersRepositoryImpl): CountersRepository
}