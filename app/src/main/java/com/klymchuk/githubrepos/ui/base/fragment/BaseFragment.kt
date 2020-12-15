package com.klymchuk.githubrepos.ui.base.fragment


import android.content.Context
import android.os.Bundle
import android.view.View
import com.klymchuk.githubrepos.utils.logTag
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.klymchuk.githubrepos.App
import com.klymchuk.githubrepos.navigation.Destination
import com.klymchuk.githubrepos.ui.base.interfaces.GlobalFragmentContext
import com.klymchuk.githubrepos.utils.Reporter
import javax.inject.Inject

abstract class BaseFragment(@LayoutRes layout: Int = 0) : Fragment(layout), GlobalFragmentContext {

    private val logTag = logTag()

    var currentActivityContext: GlobalFragmentContext? = null
        protected set

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected open fun initUI() = Unit

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val app = requireActivity().application as App
        app.component!!.inject(this)

        Reporter.appAction(logTag, "onAttach")

        try {
            currentActivityContext = context as GlobalFragmentContext
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement FragmentInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Reporter.appAction(logTag, "onDetach")
        currentActivityContext = null
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


    //==============================================================================================
    // *** GlobalFragmentContext ***
    //==============================================================================================
    override fun onBack(): Boolean {
        Reporter.appAction(logTag, "onBack")
        return false
    }

    override fun onKeyBoardOpened(isOpen: Boolean) {
        Reporter.appAction(logTag, "onKeyBoardOpened")
    }
}


//==============================================================================================
// *** Extensions ***
//==============================================================================================
fun Fragment.requireArgString(key: String): String {
    return arguments?.getString(key) ?: throw IllegalArgumentException("Missing arg: $key")
}

fun Fragment.requireArgBoolean(key: String): Boolean {
    return arguments?.getBoolean(key) ?: throw IllegalArgumentException("Missing arg: $key")
}

/**
 * Instantiate ViewModel and set common arguments to it
 */
inline fun <reified VM : BaseViewModel> BaseFragment.newViewModelWithArgs(): VM {
    return ViewModelProvider(this, viewModelFactory).get(VM::class.java).apply {
        setId(requireArgString(Destination.ARG_ID))
    }
}