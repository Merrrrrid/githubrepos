package com.klymchuk.githubrepos.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.IBinder
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlin.math.roundToInt

fun hideKeyboard(activity: Activity, windowToken: IBinder) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun showKeyboard(_activity: Activity?, _view: EditText?) {
    if (_activity != null && _view != null) {
        val imm = _activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(_view, 0)
    }
}

fun EditText.setTextSilently(text: CharSequence, listener: TextWatcher?) {
    if (listener == null) {
        setText(text)
        return
    }
    removeTextChangedListener(listener)
    setText(text)
    addTextChangedListener(listener)
}


//==============================================================================================
// *** Cyclic-Safe(Recursion) Setters ***
//==============================================================================================
fun TextView.setTextIfDistinct(text: String) {
    if (this.text.toString() != text) setText(text)
}

fun EditText.setTextIfDistinct(text: String) {
    if (this.text.toString() != text) setText(text)
}

fun CompoundButton.setCheckedIfDistinct(value: Boolean) {
    if (isChecked != value) {
        isChecked = value
    }
}

fun AbsSpinner.setSelectedPositionIfDistinct(value: Int) {
    if (selectedItemPosition != value) {
        setSelection(value)
    }
}


//==============================================================================================
// *** Visibility ***
//==============================================================================================
fun View.setVisible(value: Boolean = true): View {
    val newValue = if (value) View.VISIBLE else View.GONE
    if (visibility == newValue) return this

    visibility = newValue
    return this
}

fun View.gone(): View {
    if (visibility == View.GONE) return this

    visibility = View.GONE
    return this
}

fun View.hide(): View {
    if (visibility == View.INVISIBLE) return this

    visibility = View.INVISIBLE
    return this
}

fun View.show(): View {
    if (visibility == View.VISIBLE) return this

    visibility = View.VISIBLE
    return this
}

fun View.disable(): View {
    isEnabled = false

    return this
}

fun View.enable(): View {
    isEnabled = true

    return this
}


//==============================================================================================
// *** Misc ***
//==============================================================================================
fun RecyclerView.disableItemChangeAnimation() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

fun ImageView.setTint(@ColorRes colorRes: Int?) {
    imageTintList = if (colorRes != null) {
        ColorStateList.valueOf(context.getColor(colorRes))
    } else null
}

fun Int.toDp(context: Context) = (this * context.resources.displayMetrics.density).roundToInt()
fun Float.toDp(context: Context) = (this * context.resources.displayMetrics.density).roundToInt()