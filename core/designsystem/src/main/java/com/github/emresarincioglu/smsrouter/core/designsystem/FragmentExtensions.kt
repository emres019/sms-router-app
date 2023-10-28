package com.github.emresarincioglu.smsrouter.core.designsystem

import androidx.fragment.app.Fragment
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFade

fun Fragment.setBottomNavBarVisibility(visibility: Int, transition: Transition? = MaterialFade()) {

    val navBarOwner = requireActivity() as BottomNavBarOwner
    navBarOwner.setBottomNavBarVisibility(visibility, transition)
}