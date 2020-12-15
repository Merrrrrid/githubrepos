package com.klymchuk.githubrepos.di

import android.content.Context
import androidx.room.Room
import com.klymchuk.githubrepos.data.db.AppDatabase
import com.klymchuk.githubrepos.data.network.GitHubApiService
import com.klymchuk.githubrepos.navigation.Navigator
import com.klymchuk.githubrepos.navigation.Router
import com.klymchuk.githubrepos.navigation.RouterImpl
import com.klymchuk.githubrepos.utils.AppExecutors
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context, executors: AppExecutors): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-database"
        )
            .setQueryExecutor(executors.diskExecutor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRouter(navigator: Navigator): Router {
        return RouterImpl(navigator)
    }

    @Singleton
    @Provides
    fun provideApiService(): GitHubApiService {
        return GitHubApiService.create()
    }

}