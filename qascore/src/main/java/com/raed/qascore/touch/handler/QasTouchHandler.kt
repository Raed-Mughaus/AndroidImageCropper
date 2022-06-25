package com.raed.qascore.touch.handler

import com.raed.qascore.QasContext
import com.raed.qascore.verifier.BitmapTransformationVerifier

class QasTouchHandler(
    qasContext: QasContext,
): TouchHandler {

    private val bitmapTransformationVerifier = BitmapTransformationVerifier(qasContext)
    
    private val touchHandlers = listOf(
        CroppingRectTouchHandler(qasContext),
        BitmapTransformationTouchHandler(qasContext)
    )
    
    override fun handleFirstTouch(event: TouchEvent) {
        for (touchHandler in touchHandlers) {
            touchHandler.handleFirstTouch(event)
        }
        bitmapTransformationVerifier.verify()
    }

    override fun handleTouch(event: TouchEvent) {
        for (touchHandler in touchHandlers) {
            touchHandler.handleTouch(event)
        }
        bitmapTransformationVerifier.verify()
    }

    override fun handleLastTouch(event: TouchEvent) {
        for (touchHandler in touchHandlers) {
            touchHandler.handleLastTouch(event)
        }
        bitmapTransformationVerifier.verify()
    }

    override fun cancel(event: TouchEvent) {
        for (touchHandler in touchHandlers) {
            touchHandler.cancel(event)
        }
        bitmapTransformationVerifier.verify()
    }

}
