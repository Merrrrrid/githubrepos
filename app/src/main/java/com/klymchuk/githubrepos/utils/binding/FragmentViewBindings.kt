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

/**
 * Create new [ViewBinding] associated with the [Fragment]
 */
@JvmName("viewBindingFragment")
public fun <F : Fragment, VB : ViewBinding> Fragment.viewBinding(viewBinder: (F) -> VB): ViewBindingProperty<F, VB> {
    return FragmentViewBindingProperty(viewBinder)
}

/**
 * Create new [ViewBinding] associated with the [Fragment]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewProvider Provide a [View] from the Fragment. By default call [Fragment.requireView]
 */
@JvmName("viewBindingFragment")
public inline fun <F : Fragment, VB : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> VB,
    crossinline viewProvider: (F) -> View = Fragment::requireView
): ViewBindingProperty<F, VB> {
    return viewBinding { fragment: F -> vbFactory(viewProvider(fragment)) }
}

/**
 * Create new [ViewBinding] associated with the [Fragment]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@JvmName("viewBindingFragment")
public inline fun <VB : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> VB,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<Fragment, VB> {
    return viewBinding(vbFactory) { fragment: Fragment -> fragment.requireView().findViewById(viewBindingRootId) }
}