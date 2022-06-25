package com.raed.qascore.touch.handler

import android.graphics.PointF
import com.raed.qascore.QasContext
import com.raed.qascore.touch.detector.OneFingerTranslationDetector
import com.raed.qascore.touch.detector.ScaleGestureDetector
import com.raed.qascore.touch.detector.TouchedRectSidesDetector

internal class BitmapTransformationTouchHandler(
    private val qasContext: QasContext,
): TouchHandler {

    private val fingersCenter = PointF()

    private val scaleGestureDetector = ScaleGestureDetector { scale ->
        qasContext
            .bitmapMatrix
            .postScale(scale, scale, fingersCenter.x, fingersCenter.y)
    }

    private val translationGestureDetector = OneFingerTranslationDetector { dx, dy ->
        qasContext
            .bitmapMatrix
            .postTranslate(dx, dy)
    }

    private var touchingRectSides = false

    override fun handleFirstTouch(event: TouchEvent) {
        touchingRectSides = event.isTouchingAnyRectSide()
        handleTouch(event)
    }

    override fun handleTouch(event: TouchEvent) {
        if (touchingRectSides) {
            return
        }
        fingersCenter.setToCenterOf(event)
        scaleGestureDetector.onTouchEvent(event)
        translationGestureDetector.onTouchEvent(event)
    }

    override fun handleLastTouch(event: TouchEvent) = handleTouch(event)

    override fun cancel(event: TouchEvent) = handleTouch(event)

    private fun TouchEvent.isTouchingAnyRectSide(): Boolean {
        val touchedRectSidesDetector = TouchedRectSidesDetector(qasContext.croppingRect)
        return touchedRectSidesDetector
            .findTouchRectSide(PointF(x[0], y[0])) != null
    }

}

private fun PointF.setToCenterOf(event: TouchEvent) {
    x = (event.x[0] + event.x[1]) / 2f
    y = (event.y[0] + event.y[1]) / 2f
}
