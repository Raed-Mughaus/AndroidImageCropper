package com.raed.qascore.verifier

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import com.raed.qascore.QasContext
import com.raed.qascore.model.RotatableRect
import com.raed.qascore.model.map
import kotlin.math.max

internal class BitmapTransformationVerifier(
    private val qasContext: QasContext,
) {

    private val bitmapMatrix get() = qasContext.bitmapMatrix
    private val invertedMatrix = Matrix()
    private val croppingRect = RotatableRect()

    fun verify() {
        verifyScale()
        verifyTranslation()
    }

    private fun verifyScale() {
        bitmapMatrix.invert(invertedMatrix)
        croppingRect.set(qasContext.croppingRect)
        invertedMatrix.map(croppingRect)
        val scale = max(
            croppingRect.width / qasContext.bitmap.width.toFloat(),
            croppingRect.height / qasContext.bitmap.height.toFloat(),
        ).coerceAtLeast(1f)
        bitmapMatrix.preScale(scale, scale)
    }

    private fun verifyTranslation() {
        bitmapMatrix.invert(invertedMatrix)
        val bitmap = qasContext.bitmap
        croppingRect.set(qasContext.croppingRect)
        invertedMatrix.map(croppingRect)

        val dx = if (croppingRect.left < 0) {
            croppingRect.left
        } else if (croppingRect.right > bitmap.width) {
            croppingRect.right - bitmap.width
        } else {
            0f
        }

        val dy = if (croppingRect.top < 0) {
            croppingRect.top
        } else if (croppingRect.bottom > bitmap.height) {
            croppingRect.bottom - bitmap.height
        } else {
            0f
        }

        bitmapMatrix.preTranslate(dx, dy)
    }

}
