package com.raed.qas.ktx

import android.view.MotionEvent
import com.raed.qascore.touch.handler.TouchEvent
import com.raed.qascore.touch.handler.TouchEvent.Action
import kotlin.math.min

fun TouchEvent.set(event: MotionEvent) {
    pointersCount = min(event.pointerCount, maxPointersCount)
    for (i in 0 until pointersCount) {
        x[i] = event.getX(i)
        y[i] = event.getY(i)
        ptrId[i] = event.getPointerId(i)
    }
    action = when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> Action.Down
        MotionEvent.ACTION_MOVE -> Action.Move
        MotionEvent.ACTION_UP -> Action.Up
        MotionEvent.ACTION_POINTER_DOWN -> Action.PointerDown
        MotionEvent.ACTION_POINTER_UP -> Action.PointerUp
        MotionEvent.ACTION_CANCEL -> Action.Cancel
        else -> throw RuntimeException()
    }
    actionIndex = event.actionIndex
}
