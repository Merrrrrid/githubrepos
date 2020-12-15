package com.klymchuk.githubrepos.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klymchuk.githubrepos.ui.base.DefaultViewModelFactory
import com.klymchuk.githubrepos.ui.main.login.LoginViewModel
import com.klymchuk.githubrepos.ui.main.repos.ReposViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindsLoginViewModel(vm: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReposViewModel::class)
    abstract fun bindsReposViewModel(vm: ReposViewModel): ViewModel

    @Binds
    abstract fun bindDefaultViewModelFactory(factory: DefaultViewModelFactory): ViewModelProvider.Factory

}