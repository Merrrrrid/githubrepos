package com.klymchuk.githubrepos.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

interface Navigation {
    sealed class Command {
        class Transaction(val commands: List<Command>) : Command()

        class Add(
            val destination: Destination,
            val modifier: Modifier? = null,
        ) : Command()

        class Back(
            val destination: Destination? = null,
        ) : Command()

        class NewRoot(
            val destination: Destination,
            val modifier: Modifier? = null,
        ) : Command() {
            fun toAdd() = Add(destination, modifier)
        }

        class Replace(
            val destination: Destination,
            val modifier: Modifier? = null,
        ) : Command() {
            fun toAdd() = Add(destination, modifier)
        }
    }

    interface Modifier {
        fun onTransaction(
            transaction: FragmentTransaction,
            fragmentFrom: Fragment?,
            fragmentTo: Fragment,
        )
    }
}

interface Destination {

    companion object{
        const val ARG_ID = "destination_id"
    }

    fun id(): String

    /**
     * Addition to [id]. Consider app with AlbumScreen and SongScreen,
     * user can open multiple SongScreens. We need to distinguish them.
     * Let's use **compositeId** of two part, constant [id] and
     * dynamic(determined at runtime) [idSuffix].
     */
    fun idSuffix(): String? = null

    // todo consider case with dialogFragment?
    fun newFragment(): Fragment

    fun toId(): String {
        if (idSuffix() == null) return id()

        return "${id()}_${idSuffix()}"
    }
}