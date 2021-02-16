package com.klymchuk.githubrepos.di

import android.content.Context
import androidx.room.Room
import com.klymchuk.githubrepos.data.db.AppDatabase
import com.klymchuk.githubrepos.data.network.GitHubApiService
import com.klymchuk.githubrepos.data.network.GitHubAuthService
import com.klymchuk.githubrepos.utils.AppExecutors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context, executors: AppExecutors): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app-database"
    ).setQueryExecutor(executors.diskExecutor).build()


    @Singleton
    @Provides
    fun provideApiService(): GitHubApiService {
        return GitHubApiService.create()
    }

    @Singleton
    @Provides
    fun provideAuthService(): GitHubAuthService {
        return GitHubAuthService.create()
    }

}