package com.raed.qascore.touch.detector

import com.raed.qascore.touch.handler.TouchEvent
import kotlin.math.abs
import kotlin.math.hypot

internal class ScaleGestureDetector(
    private val listener: (Float) -> Unit,
) {

    private var mCurrSpan = 0f
    private var mPrevSpan = 0f
    private var mInitialSpan = 0f
    private var mInProgress = false
    private val mSpanSlop = 0
    private val mMinSpan = 0

    fun onTouchEvent(event: TouchEvent): Boolean {
        val action = event.action
        val count = event.pointersCount
        if (action == TouchEvent.Action.Down) {
            mInProgress = false
            mInitialSpan = 0f
        }
        if (action == TouchEvent.Action.Up || action == TouchEvent.Action.Cancel) {
            return true
        }
        val configChanged =
            action == TouchEvent.Action.Down || action == TouchEvent.Action.PointerUp || action == TouchEvent.Action.PointerDown
        val pointerUp = action == TouchEvent.Action.PointerUp
        val skipIndex = if (pointerUp) event.actionIndex else -1

        // Determine focal point
        var sumX = 0f
        var sumY = 0f
        val div = if (pointerUp) count - 1 else count
        for (i in 0 until count) {
            if (skipIndex == i) continue
            sumX += event.x[i]
            sumY += event.y[i]
        }
        val focusX = sumX / div
        val focusY = sumY / div

        // Determine average deviation from focal point
        var devSumX = 0f
        var devSumY = 0f
        for (i in 0 until count) {
            if (skipIndex == i) continue

            // Convert the resulting diameter into a radius.
            devSumX += abs(event.x[i] - focusX)
            devSumY += abs(event.y[i] - focusY)
        }
        val devX = devSumX / div
        val devY = devSumY / div

        // Span is the average distance between touch points through the focal point;
        // i.e. the diameter of the circle with a radius of the average deviation from
        // the focal point.
        val spanX = devX * 2
        val spanY = devY * 2
        val span = hypot(spanX.toDouble(), spanY.toDouble()).toFloat()

        // Dispatch begin/end events as needed.
        // If the configuration changes, notify the app to reset its current state by beginning
        // a fresh scale event stream.
        val wasInProgress = mInProgress
        if (mInProgress && (span < mMinSpan || configChanged)) {
            mInProgress = false
            mInitialSpan = span
        }
        if (configChanged) {
            mCurrSpan = span
            mPrevSpan = mCurrSpan
            mInitialSpan = mPrevSpan
        }
        val minSpan = mMinSpan
        if (!mInProgress && span >= minSpan &&
            (wasInProgress || abs(span - mInitialSpan) > mSpanSlop)
        ) {
            mCurrSpan = span
            mPrevSpan = mCurrSpan
            mInProgress = true
        }

        // Handle motion; focal point and span/scale factor are changing.
        if (action == TouchEvent.Action.Move) {
            mCurrSpan = span
            if (mInProgress) {
                val scale = if (mPrevSpan > 0) mCurrSpan / mPrevSpan else 1f
                listener(scale)
            }
            mPrevSpan = mCurrSpan
        }
        return true
    }

}