package com.klymchuk.githubrepos.ui.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    private val logTag = logTag()

    protected open fun initUI() = Unit

    override fun onAttach(context: Context) {
        super.onAttach(context)

        Reporter.appAction(logTag, "onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Reporter.appAction(logTag, "onDetach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Reporter.appAction(logTag, "onCreate")
    }

    override fun onResume() {
        super.onResume()
        Reporter.appAction(logTag, "onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Reporter.appAction(logTag, "onViewCreated")

        initUI()
    }

    override fun onPause() {
        super.onPause()
        Reporter.appAction(logTag, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Reporter.appAction(logTag, "onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Reporter.appAction(logTag, "onDestroyView")
    }
}