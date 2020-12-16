package com.klymchuk.githubrepos.ui.base.fragment

import androidx.lifecycle.ViewModel
import com.klymchuk.githubrepos.App
import com.klymchuk.githubrepos.utils.Logger
import com.klymchuk.githubrepos.utils.lifecycle.ViewModelTracker
import com.klymchuk.githubrepos.utils.logTag
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    private val logTag = logTag()
    val mViewModelTracker: ViewModelTracker

    val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    protected lateinit var mId: String

    init {
        Logger.d(logTag, "[VM] init hash: ${hashCode()}")
        mViewModelTracker = App.instance.component!!.provideViewModelTracker()
    }

    fun setId(id: String) {
        Logger.d(logTag, "[VM] setId $id hash: ${hashCode()}")
        mId = id
        mViewModelTracker.onVmCreated(this)
    }

    fun id() = mId

    override fun onCleared() {
        Logger.d(logTag, "[VM] onCleared hash: ${hashCode()}")
        mViewModelTracker.onVmDestroyed(this)
        mCompositeDisposable.clear()
        super.onCleared()
    }
}