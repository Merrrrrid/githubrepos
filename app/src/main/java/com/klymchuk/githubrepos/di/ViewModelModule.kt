package com.klymchuk.githubrepos.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klymchuk.githubrepos.ui.base.DefaultViewModelFactory
import com.klymchuk.githubrepos.ui.main.history.HistoryViewModel
import com.klymchuk.githubrepos.ui.main.repos.ReposViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReposViewModel::class)
    abstract fun bindsReposViewModel(vm: ReposViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindsHistoryViewModel(vm: HistoryViewModel): ViewModel

    @Binds
    abstract fun bindDefaultViewModelFactory(factory: DefaultViewModelFactory): ViewModelProvider.Factory

}