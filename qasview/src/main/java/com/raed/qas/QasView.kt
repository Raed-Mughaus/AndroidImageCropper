package com.raed.qas

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.raed.qas.ktx.set
import com.raed.qascore.QasContext
import com.raed.qascore.touch.handler.QasTouchHandler
import com.raed.qascore.touch.handler.TouchEvent
import com.raed.qascore.touch.handler.TouchHandler
import kotlin.math.roundToInt

class QasView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
): View(context, attrs, defStyleAttr) {

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet? = null): this(context, attrs, 0)

    private val touchEvent = TouchEvent()

    private var renderer: ViewRenderer? = null
    private var touchHandler: TouchHandler? = null

    private var duringAnimation = false

    private var qasContext: QasContext? = null
        set(qasData) {
            field = qasData
            if (qasData == null) {
                touchHandler = null
                renderer = null
            } else {
                touchHandler = QasTouchHandler(qasData)
                renderer = ViewRenderer(qasData)
            }
        }

    var bitmap: Bitmap?
        set(value) {
            qasContext = if (value != null) {
                QasContext(
                    value,
                    Matrix(),
                    Rect(),
                    context.applicationContext,
                )
            } else {
                null
            }
            initializeBitmapMatrixAndCroppingRect()
        }
        get() = qasContext?.bitmap

    var bitmapRotation: Float
        set(value) {
            qasContext?.minorRotation = value
            invalidate()
        }
        get() = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeBitmapMatrixAndCroppingRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        renderer?.render(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (duringAnimation) {
            return false
        }
        touchEvent.set(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchHandler?.handleFirstTouch(touchEvent)
            }
            MotionEvent.ACTION_UP -> {
                touchHandler?.handleLastTouch(touchEvent)

                val padding = dpToPx(48).toFloat()
                CroppingRectTransformationAnimator(
                    qasContext!!,
                    RectF(padding, padding, width - padding, height - padding),
                    onStart = {
                        duringAnimation = true
                    },
                    onUpdate = ::invalidate,
                    onEnd = {
                        duringAnimation = false
                    }
                ).start()
            }
            MotionEvent.ACTION_CANCEL -> {
                touchHandler?.cancel(touchEvent)
            }
            else -> {
                touchHandler?.handleTouch(touchEvent)
            }
        }
        invalidate()
        return true
    }

    fun cropBitmap(): Bitmap {
        TODO()
    }

    private fun initializeBitmapMatrixAndCroppingRect() {
        if (qasContext == null) {
            return
        }
        val bitmap = qasContext!!.bitmap
        qasContext!!.bitmapMatrix.setRectToRect(
            RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            Matrix.ScaleToFit.CENTER,
        )
        qasContext!!.croppingRect.set(0, 0, bitmap.width, bitmap.height)
        qasContext!!.bitmapMatrix.map(qasContext!!.croppingRect)

        qasContext!!.bitmapMatrix.postRotate(45f)
    }

}

private fun Matrix.map(rect: Rect) {
    val rectF = RectF(rect)
    mapRect(rectF)
    rect.left = rectF.left.roundToInt()
    rect.top = rectF.top.roundToInt()
    rect.right = rectF.right.roundToInt()
    rect.bottom = rectF.bottom.roundToInt()
}

fun dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        Resources.getSystem().displayMetrics,
    ).roundToInt()
}

private class CroppingRectTransformationAnimator(
    private val qasContext: QasContext,
    private val goalCroppingRect: RectF,
    private val onStart: () -> Unit,
    private val onUpdate: () -> Unit,
    private val onEnd: () -> Unit,
) {

    private val handler = Handler(Looper.getMainLooper(), this::handleMessage)

    private val initialBitmapMatrix = Matrix(qasContext.bitmapMatrix)
    private val initialCroppingRect = Rect(qasContext.croppingRect)
    private val initialCroppingRectF = RectF(initialCroppingRect)

    private val matrix = Matrix()
    private val interpolatedRect = RectF()

    fun start() {
        initialBitmapMatrix.set(qasContext.bitmapMatrix)
        handler.post { onStart() }
        for (i in 1..100) {
            val t = i / 100f
            handler.sendMessageDelayed(
                handler.obtainMessage(0, i, i),
                (t * 250).toLong(),
            )
        }
        handler.post { onEnd() }
    }

    private fun handleMessage(message: Message): Boolean {
        val t = message.arg1 / 100f
        lerp(initialCroppingRectF, goalCroppingRect, interpolatedRect, t)
        matrix.setRectToRect(
            initialCroppingRectF,
            interpolatedRect,
            Matrix.ScaleToFit.CENTER,
        )
        matrix.map(
            qasContext
                .croppingRect
                .apply { set(initialCroppingRect) }
        )
        qasContext.bitmapMatrix
            .apply { set(initialBitmapMatrix) }
            .postConcat(matrix)
        onUpdate()
        return true
    }

    private fun lerp(a: RectF, b: RectF, result: RectF, t: Float) {
        result.set(
            a.left + t * (b.left - a.left),
            a.top + t * (b.top - a.top),
            a.right + t * (b.right - a.right),
            a.bottom + t * (b.bottom - a.bottom),
        )
    }

}
