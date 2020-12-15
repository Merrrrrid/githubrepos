package com.klymchuk.githubrepos.navigation

import androidx.annotation.MainThread
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.klymchuk.githubrepos.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() : DefaultLifecycleObserver {

    companion object {
        const val POP_FLAG_DEFAULT = 0
        const val POP_FLAG_INCLUSIVE = FragmentManager.POP_BACK_STACK_INCLUSIVE
    }

    private val mCommands = mutableListOf<Navigation.Command>()

    @MainThread
    fun enqueue(command: Navigation.Command) {
        mCommands.add(command)

        if (canExecute) {
            executeCommands()
        }
    }

    private fun executeCommands() {
        if (mCommands.isEmpty()) return

        mFragmentManager?.executePendingTransactions()

        val iterator = mCommands.iterator()
        while (iterator.hasNext()) {
            executeCommand(iterator.next())
        }
        mCommands.clear()
    }

    private fun executeCommand(command: Navigation.Command) {
        when (command) {
            is Navigation.Command.Transaction -> command.commands.forEach { executeCommand(it) }
            is Navigation.Command.Add -> add(mFragmentManager!!, command)
            is Navigation.Command.Replace -> replace(mFragmentManager!!, command)
            is Navigation.Command.Back -> back(mFragmentManager!!, command)
            is Navigation.Command.NewRoot -> newRoot(mFragmentManager!!, command)
        }
    }


    //==============================================================================================
    // *** Commands ***
    //==============================================================================================
    private val mContainerResId: Int = com.klymchuk.githubrepos.R.id.fragmentContainer // todo mContainerId
    private var mActivity: MainActivity? = null
    private var mFragmentManager: FragmentManager? = null

    private fun add(fragmentManager: FragmentManager, command: Navigation.Command.Add) {
        val tag = command.destination.toId()
        val fragment = command.destination.newFragmentWithIdArg()

        fragmentManager.commit {
            command.modifier?.onTransaction(
                this,
                fragmentManager.findFragmentById(mContainerResId),
                fragment,
            )

            addToBackStack(tag)
            setReorderingAllowed(true)
            add(mContainerResId, fragment, tag)
        }
    }

    private fun replace(fragmentManager: FragmentManager, command: Navigation.Command.Replace) {
        val tag = command.destination.toId()
        val fragment = command.destination.newFragmentWithIdArg()

        fragmentManager.commit {
            command.modifier?.onTransaction(
                this,
                fragmentManager.findFragmentById(mContainerResId),
                fragment,
            )

            addToBackStack(tag)
            setReorderingAllowed(true)
            replace(mContainerResId, fragment, tag)
        }
    }

    private fun back(fragmentManager: FragmentManager, command: Navigation.Command.Back) {
        val tagToBack: String? = command.destination?.toId()

        if (isLastDestination()) {
            val consumed = finishAppListener?.onFinish()

            if (consumed != true) {
                finishApp()
            }
        } else {
            fragmentManager.popBackStack(tagToBack, POP_FLAG_DEFAULT)
        }
    }

    private fun newRoot(fragmentManager: FragmentManager, command: Navigation.Command.NewRoot) {
        fragmentManager.popBackStack(null, POP_FLAG_INCLUSIVE)
        add(fragmentManager, command.toAdd())
    }


    //==============================================================================================
    // *** Finish App ***
    //==============================================================================================
    interface FinishAppListener {

        /**
         * return true if consumed
         */
        fun onFinish(): Boolean
    }

    var finishAppListener: FinishAppListener? = null

    fun finishApp(fragmentManager: FragmentManager = mFragmentManager!!) {
        // Let fragment go through it's lifecycle before exit App
        // Important for some callbacks
        fragmentManager.popBackStack()

        mActivity!!.finish()
    }


    //==============================================================================================
    // *** Lifecycle ***
    //==============================================================================================
    private var mOwner: LifecycleOwner? = null
    private var canExecute = true

    fun observe(owner: LifecycleOwner, activity: MainActivity, fragmentManager: FragmentManager) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        mOwner = owner
        mActivity = activity
        mFragmentManager = fragmentManager
        owner.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        canExecute = true
        executeCommands()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        canExecute = true
    }

    override fun onPause(owner: LifecycleOwner) {
        canExecute = false

        super.onPause(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        mOwner = null
        mActivity = null
        mFragmentManager = null
    }


    //==============================================================================================
    // *** utils ***
    //==============================================================================================
    private fun isLastDestination() = mFragmentManager!!.backStackEntryCount == 1
}