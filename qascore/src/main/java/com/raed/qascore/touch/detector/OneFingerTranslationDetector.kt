package com.raed.qascore.touch.detector

import com.raed.qascore.touch.handler.TouchEvent

class OneFingerTranslationDetector(
    private var onTranslation: (dx: Float, dy: Float) -> Unit,
) {

    private var mLastX = 0f
    private var mLastY = 0f
    private var ptrId = 0
    private var ignoreEvents = false

    fun onTouchEvent(event: TouchEvent) {
        if (event.action == TouchEvent.Action.Down) {
            ignoreEvents = false
        }
        if (ignoreEvents) {
            return
        }
        when (event.action) {
            TouchEvent.Action.Down -> {
                mLastX = event.x[0]
                mLastY = event.y[0]
                ptrId = event.ptrId[0]
            }
            TouchEvent.Action.Move, TouchEvent.Action.Up -> {
                val ptrIdx = event.findPtrIdx(ptrId)
                if (ptrIdx < 0) {
                    ignoreEvents = true
                    return
                }
                val xTranslation = event.x[ptrIdx] - mLastX
                mLastX = event.x[ptrIdx]
                val yTranslation = event.y[ptrIdx] - mLastY
                mLastY = event.y[ptrIdx]
                onTranslation(xTranslation, yTranslation)
            }
            else -> { }
        }
    }
}
