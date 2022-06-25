package com.raed.qascore.touch.handler

interface TouchHandler {

    fun handleFirstTouch(event: TouchEvent)

    fun handleTouch(event: TouchEvent)

    fun handleLastTouch(event: TouchEvent)

    fun cancel(event: TouchEvent)

}
