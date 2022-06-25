package com.raed.qas

import android.graphics.*
import com.raed.qascore.QasContext

class ViewRenderer(
    private val qasContext: QasContext,
) {

    private val croppingRectPaint = Paint().apply {
        color = 0
        alpha = 200
    }

    private val screenRectPath = Path()
    private val croppingRectPath = Path()
    private val rectF = RectF()

    fun render(canvas: Canvas) {
        canvas.drawQasBitmap()
        canvas.drawCroppingRect()
    }

    private fun Canvas.drawQasBitmap() {
        save()
        setMatrix(qasContext.bitmapMatrix)
        drawBitmap(qasContext.bitmap, 0f, 0f, null)
        restore()
    }

    private fun Canvas.drawCroppingRect() {
        save()
        rectF.set(qasContext.croppingRect)
        croppingRectPath.reset()
        croppingRectPath.addRect(rectF, Path.Direction.CW)
        screenRectPath.reset()
        screenRectPath.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        screenRectPath.op(croppingRectPath, Path.Op.DIFFERENCE)
        clipPath(screenRectPath)
        drawPaint(croppingRectPaint)
        restore()
    }

}
