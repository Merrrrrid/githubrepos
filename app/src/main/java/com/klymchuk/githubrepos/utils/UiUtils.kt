package com.klymchuk.githubrepos.utils

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

fun hideKeyboard(activity: Activity, windowToken: IBinder) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

//==============================================================================================
// *** Visibility ***
//==============================================================================================

fun View.gone(): View {
    if (visibility == View.GONE) return this

    visibility = View.GONE
    return this
}

fun View.show(): View {
    if (visibility == View.VISIBLE) return this

    visibility = View.VISIBLE
    return this
}

//==============================================================================================
// *** Misc ***
//==============================================================================================
fun RecyclerView.disableItemChangeAnimation() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}