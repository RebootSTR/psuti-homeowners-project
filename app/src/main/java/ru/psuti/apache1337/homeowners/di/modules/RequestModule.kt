package ru.psuti.apache1337.homeowners.di.modules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import ru.psuti.apache1337.homeowners.data.demo.RequestsDemo
import ru.psuti.apache1337.homeowners.data.requests.db.RequestsDatabase
import ru.psuti.apache1337.homeowners.data.requests.db.dao.RequestDao
import ru.psuti.apache1337.homeowners.data.requests.remote.RequestService
import ru.psuti.apache1337.homeowners.data.requests.repository.RequestRepositoryImpl
import ru.psuti.apache1337.homeowners.domain.requests.db.repository.RequestRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Provides {

    @Provides
    @Singleton
    fun provideRequestDatabase(
        @ApplicationContext appContext: Context
    ): RequestsDatabase {
        val db = Room.databaseBuilder(
            appContext,
            RequestsDatabase::class.java,
            "Requests.db").build()

        CoroutineScope(Dispatchers.IO).launch {
            db.requestDao().saveAll(RequestsDemo.demoRequests)
        }

        return db
    }

    @Provides
    @Singleton
    fun provideRequestDao( database: RequestsDatabase): RequestDao {
        return database.requestDao()
    }

    @Provides
    @Singleton
    fun provideRequestService(retrofit: Retrofit): RequestService {
        return retrofit.create(RequestService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface Binds {

    @Binds
    @Singleton
    fun bindRequestRepository(rep: RequestRepositoryImpl): RequestRepository

}