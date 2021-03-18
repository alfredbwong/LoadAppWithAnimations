package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var downloadClickedColor = 0
    private var downloadLoadingColor = 0
    private var downloadCompletedColor = 0

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
        textAlign = Paint.Align.CENTER
    }
    //Paint for the bg of the loading button
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
//        when (new){
//            ButtonState.Loading -> {
//                bgPaint.color = Color.YELLOW
//            }
//            ButtonState.Clicked -> {
//                bgPaint.color = Color.RED
//            }
//            ButtonState.Completed -> {
//                bgPaint.color = Color.GREEN
//            }
//        }
    }


    init {
        isClickable = true

        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            downloadClickedColor = getColor (R.styleable.LoadingButton_downloadClicked, 0)
            downloadLoadingColor = getColor (R.styleable.LoadingButton_downloadLoading, 0)
            downloadCompletedColor = getColor (R.styleable.LoadingButton_downloadCompleted, 0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bgPaint.color = when (buttonState){
            ButtonState.Loading -> downloadLoadingColor
            ButtonState.Completed -> downloadCompletedColor
            ButtonState.Clicked -> downloadClickedColor
        }
        canvas.drawColor(bgPaint.color)

        val textPositionX = canvas.width /2
        val textPositionY = canvas.height /2 - (paint.descent() + paint.ascent())/2

        canvas.drawText("HELLO", textPositionX.toFloat(), textPositionY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = buttonState.next()
        invalidate()
        return true
    }
}