package com.klymchuk.githubrepos.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.klymchuk.githubrepos.ui.main.login.LoginFragment
import com.klymchuk.githubrepos.ui.main.repos.ReposFragment

object Destinations {

    fun login() = object : Destination {
        override fun id() = "Login"
        override fun newFragment() = LoginFragment()
    }

    fun repos() = object : Destination {
        override fun id() = "Repos"
        override fun newFragment() = ReposFragment()
    }

}


//==============================================================================================
// *** Extensions ***
//==============================================================================================
fun Destination.newFragmentWithIdArg(): Fragment {
    return newFragment().apply {
        val args = arguments ?: Bundle()

        args.putString(Destination.ARG_ID, toId())
        arguments = args
    }
}