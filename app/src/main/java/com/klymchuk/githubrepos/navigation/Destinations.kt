package com.klymchuk.githubrepos.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.klymchuk.githubrepos.ui.main.history.HistoryFragment
import com.klymchuk.githubrepos.ui.main.repos.ReposFragment

object Destinations {

    fun repos() = object : Destination {
        override fun id() = "Repos"
        override fun newFragment() = ReposFragment()
    }

    fun history() = object : Destination {
        override fun id() = "History"
        override fun newFragment() = HistoryFragment()
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