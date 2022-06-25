package com.raed.qascore.verifier

import android.graphics.Rect
import com.raed.qascore.model.RectSide
import com.raed.qascore.model.RectSide.*
import com.raed.qascore.model.Ratio
import kotlin.math.roundToInt

internal class CroppingRectVerifier(
    private val rect: Rect,
    private val ratio: Ratio,
    private val minSize: Int,
) {

    fun onRectSideChanged(side: RectSide) {
        rect.verifySize(side)
        rect.verifyRatio(side)
    }

    private fun Rect.verifySize(side: RectSide) {
        when (side) {
            Left, LeftTop, LeftBottom -> {
                left = left.coerceAtMost(right - minSize)
            }
            Top -> {
                top = top.coerceAtMost(bottom - minSize)
            }
            Right, RightTop, RightBottom -> {
                right = right.coerceAtLeast(left + minSize)
            }
            Bottom ->  {
                bottom = bottom.coerceAtLeast(top + minSize)
            }
        }
    }

    private fun Rect.verifyRatio(side: RectSide) {
        when (side) {
            Left -> {
                updateHeight(exactCenterY())
            }
            Top -> {
                updateWidth(exactCenterX())
            }
            Right -> {
                updateHeight(exactCenterY())
            }
            Bottom ->  {
                updateWidth(exactCenterX())
            }
            LeftTop -> {
                updateHeight(bottom.toFloat())
            }
            LeftBottom -> {
                updateHeight(top.toFloat())
            }
            RightTop -> {
                updateHeight(bottom.toFloat())
            }
            RightBottom -> {
                updateHeight(top.toFloat())
            }
        }
    }

    private fun Rect.updateWidth(cx: Float) {
        if (ratio !is Ratio.Fixed) {
            return
        }
        val newWidth = ratio.antecedent / ratio.consequent.toFloat() * height()
        val scale = newWidth / width()
        scale(scale, 1f, cx, 0f)
    }

    private fun Rect.updateHeight(cy: Float) {
        if (ratio !is Ratio.Fixed) {
            return
        }
        val newHeight = ratio.consequent / ratio.antecedent.toFloat() * width()
        val scale = newHeight / height()
        scale(1f, scale, 0f, cy)
    }

}

private fun Rect.scale(xScale: Float, yScale: Float, cx: Float, cy: Float) = set(
    (cx + xScale * (left - cx)).roundToInt(),
    (cy + yScale * (top - cy)).roundToInt(),
    (cx + xScale * (right - cx)).roundToInt(),
    (cy + yScale * (bottom - cy)).roundToInt(),
)
