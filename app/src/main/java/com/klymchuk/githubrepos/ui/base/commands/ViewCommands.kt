package com.klymchuk.githubrepos.ui.base.commands

import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.klymchuk.githubrepos.utils.logTag

interface Command<View> {
    fun execute(view: View)
}

@MainThread
inline fun <reified V> ViewCommandProcessor<V>.enqueue(crossinline command: (view: V) -> Unit) {
    enqueue(object : Command<V> {
        override fun execute(view: V) {
            command.invoke(view)
        }
    })
}

/**
 * Lifecycle-aware action processor. Executes actions when View is [Lifecycle.State.RESUMED]
 * or put them into queue and do it later.
 *
 * For stateless actions (Side Effects) e.g. showToast or hideKeyboard.
 * Replacement for so called "SingleLiveEvent" in MVVM
 */
open class ViewCommandProcessor<View> : DefaultLifecycleObserver {

    private val logTag = logTag()

    private val mCommands = mutableListOf<Command<View>>()

    @MainThread
    fun enqueue(command: Command<View>) {
//        Logger.d(logTag, "enqueue")
        mCommands.add(command)

        if (mOwner?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true) {
            executeCommands()
        }
    }

    private fun executeCommands() {
//        Logger.d(logTag, "${mView!!::class.java.simpleName} executeCommands size: ${mCommands.size}")

        if (mCommands.isEmpty()) return

        val iterator = mCommands.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()

            command.execute(mView!!)
        }
        mCommands.clear()

    }


    //==============================================================================================
    // *** Lifecycle ***
    //==============================================================================================
    private var mOwner: LifecycleOwner? = null
    private var mView: View? = null

    fun observe(owner: LifecycleOwner, view: View) {
//        Logger.d(logTag, "observe ${view!!::class.java.simpleName}")

        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        mOwner = owner
        mView = view
        owner.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        executeCommands()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        mOwner = null
        mView = null
    }
}