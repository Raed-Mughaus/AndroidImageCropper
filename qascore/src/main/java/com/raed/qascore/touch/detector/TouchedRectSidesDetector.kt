package com.raed.qascore.touch.detector

import android.content.res.Resources
import android.graphics.PointF
import android.graphics.Rect
import android.util.TypedValue
import com.raed.qascore.model.RectSide
import kotlin.math.abs
import kotlin.math.roundToInt

class TouchedRectSidesDetector(
    private val rect: Rect,
) {

    private val touchArea = dpToPx(48)

    fun findTouchRectSide(point: PointF): RectSide? {
        val touchedSides = mutableSetOf<RectSide>()
        if (isTouchingLeftSide(point)) {
            touchedSides.add(RectSide.Left)
        }
        if (isTouchingTopSide(point)) {
            touchedSides.add(RectSide.Top)
        }
        if (isTouchingRightSide(point)) {
            touchedSides.add(RectSide.Right)
        }
        if (isTouchingBottomSide(point)) {
            touchedSides.add(RectSide.Bottom)
        }
        return when {
            touchedSides.size == 0 -> {
                null
            }
            touchedSides.size == 1 -> {
                touchedSides.single()
            }
            RectSide.Left in touchedSides && RectSide.Top in touchedSides-> {
                RectSide.LeftTop
            }
            RectSide.Left in touchedSides && RectSide.Bottom in touchedSides-> {
                RectSide.LeftBottom
            }
            RectSide.Right in touchedSides && RectSide.Top in touchedSides-> {
                RectSide.RightTop
            }
            RectSide.Right in touchedSides && RectSide.Bottom in touchedSides-> {
                RectSide.RightBottom
            }
            else -> {
                throw RuntimeException()
            }
        }
    }

    private fun isTouchingLeftSide(point: PointF): Boolean {
        return abs(rect.left - point.x) < touchArea &&
                (point.y > rect.top - touchArea / 2) &&
                (point.y < rect.bottom + touchArea / 2)
    }

    private fun isTouchingTopSide(point: PointF): Boolean {
        return abs(rect.top - point.y) < touchArea &&
                (point.x > rect.left - touchArea / 2) &&
                (point.x < rect.right + touchArea / 2)
    }

    private fun isTouchingRightSide(point: PointF): Boolean {
        return abs(rect.right - point.x) < touchArea &&
                (point.y > rect.top - touchArea / 2) &&
                (point.y < rect.bottom + touchArea / 2)
    }

    private fun isTouchingBottomSide(point: PointF): Boolean {
        return abs(rect.bottom - point.y) < touchArea &&
                (point.x > rect.left - touchArea / 2) &&
                (point.x < rect.right + touchArea / 2)
    }

}

private fun dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        Resources.getSystem().displayMetrics,
    ).roundToInt()
}
