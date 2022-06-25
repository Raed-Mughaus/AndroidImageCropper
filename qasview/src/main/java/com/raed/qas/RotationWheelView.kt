package com.raed.qas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.raed.qas.ktx.set
import com.raed.qascore.touch.detector.OneFingerTranslationDetector
import com.raed.qascore.touch.handler.TouchEvent
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class RotationWheelView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
): View(context, attrs, defStyleAttr) {

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    private val paint = Paint().apply {
        color = -1
        strokeWidth = 3f
        textSize = dpToPx(14).toFloat()
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
    }

    private val triangleSize = dpToPx(8).toFloat()
    private val trianglePath = Path().apply {
        moveTo(0f, triangleSize)
        lineTo(triangleSize / 2, 0f)
        lineTo(triangleSize, triangleSize)
        close()
    }

    private val textBottomMargin = dpToPx(6).toFloat()
    private val largePointRadius = dpToPx(3).toFloat()
    private val smallPointRadius = dpToPx(1).toFloat()

    private var radius = 0f

    private val translationDetector = OneFingerTranslationDetector { dx, _ ->
        rotationDegrees -= 60 * dx / radius
        rotationDegrees = rotationDegrees.coerceIn(-45f, 44f)
        onRotationChanged?.invoke(roundedRotationDegrees)
    }

    private var rotationDegrees = 0f

    private val roundedRotationDegrees: Float
        get() {
            val intRotationDegrees = rotationDegrees.roundToInt()
            return if (intRotationDegrees % 10 == 0 && abs(rotationDegrees - intRotationDegrees) < 0.5f) {
                intRotationDegrees.toFloat()
            } else {
                rotationDegrees
            }
        }

    private val touchEvent = TouchEvent()

    var onRotationChanged: ((Float) -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val scale = min(1f, (w * 0.2f) / h)
        radius = scale * w.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(
            (width - triangleSize) / 2f,
            0.15f * radius + dpToPx(12),
        )
        canvas.drawPath(trianglePath, paint)
        canvas.restore()

        canvas.translate(width / 2f, -0.85f * radius)
        for (degrees in 0 until 360) {
            canvas.save()
            canvas.rotate(degrees.toFloat() + roundedRotationDegrees)
            val pointRadius = if (degrees % 10 == 0) {
                largePointRadius
            } else {
                smallPointRadius
            }
            canvas.translate(radius, 0f)
            val scale = (1 - abs(90 - degrees - roundedRotationDegrees).coerceIn(-30f, 30f) / 30f)
            paint.alpha = (255 * scale * scale).roundToInt()
            canvas.drawCircle(0f, 0f, pointRadius, paint)
            paint.alpha = 255
            if (degrees % 10 == 0) {
                canvas.rotate(-90f, 0f, 0f)
                val path = Path()
                val text = "${90 - degrees}"
                paint.getTextPath(text, 0, text.length, 0f, 0f, path)
                val textWidth = paint.measureText(text)
                canvas.translate(-textWidth/2f, -textBottomMargin)
                canvas.drawPath(path, paint)
                //canvas.drawText(text, -textWidth/2f, -textBottomMargin, paint)
            }
            canvas.restore()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val desiredWidth = dpToPx(280)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredWidth, widthSize)
            }
            MeasureSpec.UNSPECIFIED -> {
                desiredWidth
            }
            else -> {
                throw RuntimeException()
            }
        }

        val desiredHeight = (0.2f * width + dpToPx(12)).roundToInt()
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredHeight, heightSize)
            }
            MeasureSpec.UNSPECIFIED -> {
                desiredHeight
            }
            else -> {
                throw RuntimeException()
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchEvent.set(event)
        translationDetector.onTouchEvent(touchEvent)
        invalidate()
        return true
    }

}
