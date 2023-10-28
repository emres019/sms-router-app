package com.github.emresarincioglu.smsrouter.core.designsystem

import androidx.transition.Transition
import com.google.android.material.transition.MaterialFade

interface BottomNavBarOwner {
    fun setBottomNavBarVisibility(visibility: Int, transition: Transition? = MaterialFade())
}