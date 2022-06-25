package com.raed.qascore.model

import android.graphics.Matrix
import android.graphics.Rect

internal class RotatableRect {

    val points = FloatArray(8)

    fun set(rect: Rect) {
        this.points[0] = rect.left.toFloat()
        this.points[1] = rect.top.toFloat()
        this.points[2] = rect.left.toFloat()
        this.points[3] = rect.bottom.toFloat()
        this.points[4] = rect.right.toFloat()
        this.points[5] = rect.bottom.toFloat()
        this.points[6] = rect.right.toFloat()
        this.points[7] = rect.top.toFloat()
    }

    val left: Float
        get() {
            return points
                .filterIndexed { idx, _ -> idx % 2 == 0 }
                .min()
        }

    val top: Float
        get() {
            return points
                .filterIndexed { idx, _ -> idx % 2 == 1 }
                .min()
        }

    val right: Float
        get() {
            return points
                .filterIndexed { idx, _ -> idx % 2 == 0 }
                .max()
        }

    val bottom: Float
        get() {
            return points
                .filterIndexed { idx, _ -> idx % 2 == 1 }
                .max()
        }

    val width get() = right - left

    val height get() = bottom - top

}

internal fun Matrix.map(rect: RotatableRect) {
    mapPoints(rect.points)
}
