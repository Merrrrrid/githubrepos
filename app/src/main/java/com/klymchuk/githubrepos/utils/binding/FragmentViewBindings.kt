@file:Suppress("RedundantVisibilityModifier", "unused")

package com.klymchuk.githubrepos.utils.binding

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

private class FragmentViewBindingProperty<F : Fragment, VB : ViewBinding>(
    viewBinder: (F) -> VB
) : ViewBindingProperty<F, VB>(viewBinder) {

    override fun getLifecycleOwner(thisRef: F) = thisRef.viewLifecycleOwner
}

@JvmName("viewBindingFragment")
public fun <F : Fragment, VB : ViewBinding> Fragment.viewBinding(viewBinder: (F) -> VB): ViewBindingProperty<F, VB> {
    return FragmentViewBindingProperty(viewBinder)
}

@JvmName("viewBindingFragment")
public inline fun <F : Fragment, VB : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> VB,
    crossinline viewProvider: (F) -> View = Fragment::requireView
): ViewBindingProperty<F, VB> {
    return viewBinding { fragment: F -> vbFactory(viewProvider(fragment)) }
}

@JvmName("viewBindingFragment")
public inline fun <VB : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> VB,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<Fragment, VB> {
    return viewBinding(vbFactory) { fragment: Fragment -> fragment.requireView().findViewById(viewBindingRootId) }
}