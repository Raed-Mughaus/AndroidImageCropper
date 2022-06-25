package com.raed.qascore.touch.handler

class TouchEvent {

    val maxPointersCount = 5

    var pointersCount: Int = 0
    var x = FloatArray(maxPointersCount)
    var y = FloatArray(maxPointersCount)
    var ptrId = IntArray(maxPointersCount)
    var action = Action.Down
    var actionIndex: Int = 0

    enum class Action {
        Down,
        Move,
        Up,
        PointerUp,
        PointerDown,
        Cancel,
    }

    fun findPtrIdx(ptrId: Int): Int {
        for (i in 0 until pointersCount) {
            if (this.ptrId[i] == ptrId) {
                return i
            }
        }
        return -1
    }

}
