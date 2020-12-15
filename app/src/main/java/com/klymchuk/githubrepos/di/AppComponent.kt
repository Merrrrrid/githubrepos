package com.klymchuk.githubrepos.di

import android.content.Context
import com.klymchuk.githubrepos.ui.MainActivity
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.ui.login.LoginActivity
import com.klymchuk.githubrepos.utils.lifecycle.ViewModelTracker
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context)
        fun build(): AppComponent
    }


    fun provideViewModelTracker(): ViewModelTracker

    fun inject(entity: BaseFragment)
    fun inject(entity: MainActivity)
    fun inject(entity: LoginActivity)

}