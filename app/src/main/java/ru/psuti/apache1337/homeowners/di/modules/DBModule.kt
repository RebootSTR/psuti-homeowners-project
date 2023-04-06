package ru.psuti.apache1337.homeowners.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.psuti.apache1337.homeowners.data.AppDatabase
import ru.psuti.apache1337.homeowners.data.counters.db.enitites.Converters
import ru.psuti.apache1337.homeowners.data.profile.db.UserDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Users.db"
        ).addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build()
    }
}