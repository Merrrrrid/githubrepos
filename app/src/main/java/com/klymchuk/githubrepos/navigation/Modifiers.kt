package com.klymchuk.githubrepos.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.klymchuk.githubrepos.utils.applyFadeTransitions
import com.klymchuk.githubrepos.utils.applySlideTransitions

inline fun modifier(
    crossinline action: (
        transaction: FragmentTransaction,
        fragmentFrom: Fragment?,
        fragmentTo: Fragment,
    ) -> Unit
): Navigation.Modifier = object : Navigation.Modifier {
    override fun onTransaction(transaction: FragmentTransaction, fragmentFrom: Fragment?, fragmentTo: Fragment) {
        action.invoke(transaction, fragmentFrom, fragmentTo)
    }
}

fun modifierFade() = modifier { transaction, fragmentFrom, fragmentTo ->
    fragmentFrom?.applyFadeTransitions()
    fragmentTo.applyFadeTransitions()
}
