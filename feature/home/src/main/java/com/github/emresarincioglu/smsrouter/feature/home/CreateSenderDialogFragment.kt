package com.github.emresarincioglu.smsrouter.feature.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.github.emresarincioglu.smsrouter.core.designsystem.setBottomNavBarVisibility
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform

class CreateSenderDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            val surfaceColor = MaterialColors.getColor(
                requireContext(),
                com.google.android.material.R.attr.colorSurface,
                Color.TRANSPARENT
            )
            startContainerColor = surfaceColor
            endContainerColor = surfaceColor
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        setBottomNavBarVisibility(View.GONE, transition = null)
        return inflater.inflate(R.layout.fragment_create_sender, container, false)
    }
}