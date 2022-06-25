package com.raed.qascore.touch.handler

import android.graphics.PointF
import android.graphics.Rect
import com.raed.qascore.QasContext
import com.raed.qascore.model.Ratio
import com.raed.qascore.model.RectSide
import com.raed.qascore.model.RectSide.*
import com.raed.qascore.touch.detector.TouchedRectSidesDetector
import com.raed.qascore.verifier.CroppingRectVerifier
import kotlin.math.roundToInt

internal class CroppingRectTouchHandler(
    private val qasContext: QasContext,
): TouchHandler {

    private val rect get() = qasContext.croppingRect
    private var touchedSide: RectSide? = null
    private var ignoreEvent = false

    private val croppingRectVerifier = CroppingRectVerifier(
        rect,
        qasContext.ratio,
        qasContext.minCroppingRectSize,
    )

    override fun handleFirstTouch(event: TouchEvent) {
        ignoreEvent = false
        if (event.pointersCount > 1) {
            ignoreEvent = true
            return
        }
        val point = event.toPoint()
        val touchedSidesDetector = TouchedRectSidesDetector(rect)
        touchedSide = touchedSidesDetector.findTouchRectSide(point)
    }

    override fun handleTouch(event: TouchEvent) {
        if (touchedSide == null || ignoreEvent) {
            return
        }
        if (event.pointersCount > 1) {
            ignoreEvent = true
            return
        }
        val point = event.toPoint()
        rect.setSidesPosition(point)
    }

    override fun handleLastTouch(event: TouchEvent) {
        if (touchedSide == null || ignoreEvent) {
            return
        }
        if (event.pointersCount > 1) {
            ignoreEvent = true
            return
        }
        val point = event.toPoint()
        rect.setSidesPosition(point)
    }

    override fun cancel(event: TouchEvent) {

    }

    private fun Rect.setSidesPosition(eventLocation: PointF) {
        val x = eventLocation.x.roundToInt()
        val y = eventLocation.y.roundToInt()
        when (touchedSide!!) {
            Left -> left = x
            Top -> top = y
            Right -> right = x
            Bottom -> bottom = y
            LeftTop -> {
                left = x
                if (qasContext.ratio is Ratio.Custom) {
                    top = y
                }
            }
            LeftBottom -> {
                left = x
                if (qasContext.ratio is Ratio.Custom) {
                    bottom = y
                }
            }
            RightTop -> {
                right = x
                if (qasContext.ratio is Ratio.Custom) {
                    top = y
                }
            }
            RightBottom -> {
                right = x
                if (qasContext.ratio is Ratio.Custom) {
                    bottom = y
                }
            }
        }
        croppingRectVerifier.onRectSideChanged(touchedSide!!)
    }

}

private fun TouchEvent.toPoint(): PointF {
    return PointF(x[0], y[0])
}
