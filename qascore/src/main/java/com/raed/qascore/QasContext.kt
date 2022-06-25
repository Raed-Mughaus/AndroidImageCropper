package com.raed.qascore

import android.content.Context
import android.graphics.*
import android.util.Log
import com.raed.qascore.model.Ratio
import kotlin.math.*

class QasContext(
    val bitmap: Bitmap,
    val bitmapMatrix: Matrix,
    val croppingRect: Rect,
    val appContext: Context,
    val minCroppingRectSize: Int = 100,
    val ratio: Ratio = Ratio.Fixed(2, 3),
) {

    var majorRotation: Int = 0
        set(value) {
            require(majorRotation in listOf(0, 90, 180, 270))
            field = value
            updateMatrixRotation()
        }


    var minorRotation: Float = 0f
        set(value) {
            require(value in -45f..44f)
            field = value
            updateMatrixRotation()
        }

}

fun QasContext.updateMatrixRotation() {
    val cx = croppingRect.exactCenterX()
    val cy = croppingRect.exactCenterY()
    bitmapMatrix.postRotate(-bitmapMatrix.getRotationAngle(), cx, cy)
    bitmapMatrix.postRotate(majorRotation + minorRotation, cx, cy)
}

fun Matrix.getRotationAngle() = FloatArray(9)
    .apply { getValues(this) }
    .let { -atan2(it[Matrix.MSKEW_X], it[Matrix.MSCALE_X]) * (180 / PI.toFloat()) }
