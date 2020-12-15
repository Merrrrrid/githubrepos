package com.klymchuk.githubrepos.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class DefaultViewModelFactory @Inject constructor(private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: throw IllegalArgumentException("unknown model class $modelClass")
        return try {
            @Suppress("UNCHECKED_CAST")
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}