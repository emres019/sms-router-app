package com.github.emresarincioglu.smsrouter.feature.home

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.emresarincioglu.smsrouter.core.designsystem.R

/**
 * @param backgroundColorFrom Background color before exceeding the [swipeThreshold]
 * @param backgroundColorTo Background color after exceeding the [swipeThreshold]
 * @param swipeThreshold Threshold that must be crossed to trigger deletion
 */
class SwipeToDeleteHelper(
    private val context: Context,
    @ColorInt iconColor: Int = Color.WHITE,
    @ColorInt private val backgroundColorFrom: Int = Color.GRAY,
    @ColorInt private val backgroundColorTo: Int = Color.RED,
    @FloatRange(from = 0.0, to = 1.0) private val swipeThreshold: Float = 0.6f,
    private val onSwiped: (itemPosition: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {

    private val backgroundPaint = Paint()
    private val argbEvaluator = ArgbEvaluator()
    private val trashIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24)?.apply {
        setTint(iconColor)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        onSwiped(position)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        displacementX: Float,
        displacementY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val animationStartSwipePercentage = swipeThreshold - 0.15f
        backgroundPaint.color = getBackgroundColor(
            displacementX / viewHolder.itemView.width,
            animationStartSwipePercentage,
            swipeThreshold
        )

        drawBackground(canvas, viewHolder, displacementX)

        val exceedThreshold = (displacementX >= viewHolder.itemView.width * swipeThreshold)
        if (exceedThreshold) {
            trashIcon?.let {
                drawIcon(canvas, viewHolder.itemView, it)
            }
        }

        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            displacementX,
            displacementY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = swipeThreshold

    private fun drawBackground(
        canvas: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        displacementX: Float
    ) {

        val backgroundEnd = viewHolder.itemView.left + displacementX
        canvas.drawRect(
            viewHolder.itemView.left.toFloat(),
            viewHolder.itemView.top.toFloat(),
            backgroundEnd,
            viewHolder.itemView.bottom.toFloat(),
            backgroundPaint
        )
    }

    private fun drawIcon(
        canvas: Canvas,
        recyclerViewItem: View,
        icon: Drawable
    ) {

        // TODO: Add enter animation
        val marginTop = (recyclerViewItem.height - icon.intrinsicHeight) / 2
        val marginStart = dpToPx(8)
        val iconLeft = recyclerViewItem.left + marginStart
        val iconTop = recyclerViewItem.top + marginTop
        val iconRight = iconLeft + icon.intrinsicWidth
        val iconBottom = iconTop + icon.intrinsicHeight

        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(canvas)
    }

    private fun getBackgroundColor(
        @FloatRange(from = 0.0, to = 1.0) displacementXPercentage: Float,
        @FloatRange(from = 0.0, to = 1.0) animationStartSwipePercentage: Float,
        @FloatRange(from = 0.0, to = 1.0) animationEndSwipePercentage: Float
    ) = when {

        displacementXPercentage <= animationStartSwipePercentage -> backgroundColorFrom
        displacementXPercentage >= animationEndSwipePercentage -> backgroundColorTo
        else -> {
            val fraction = (displacementXPercentage - animationStartSwipePercentage) /
                    (animationEndSwipePercentage - animationStartSwipePercentage)
            argbEvaluator.evaluate(
                fraction,
                backgroundColorFrom,
                backgroundColorTo
            ) as Int
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}
