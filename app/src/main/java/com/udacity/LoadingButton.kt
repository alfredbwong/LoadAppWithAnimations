package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.renderscript.Sampler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0


    private var downloadClickedColor = 0
    private var downloadLoadingColor = 0
    private var downloadCompletedColor = 0

    private var vAnimator : ValueAnimator = ValueAnimator()

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    //Paint for the bg of the loading button
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val testPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    private var vRectStart = 0f
    private var vRectEnd = 0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new){
            ButtonState.Loading -> {
                val vAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                vAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    vRectEnd = value
                    invalidate()
                }
                vAnimator.duration = 10000L
                vAnimator.start()
            }
            else -> {
                //Reset background color
                vRectEnd = 0f
                invalidate()
            }
        }


    }


    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            downloadClickedColor = getColor(R.styleable.LoadingButton_downloadClicked, 0)
            downloadLoadingColor = getColor(R.styleable.LoadingButton_downloadLoading, 0)
            downloadCompletedColor = getColor(R.styleable.LoadingButton_downloadCompleted, 0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            drawBackGroundColor()
            drawLoadingProgressBar()
            drawText()
        }

        //Draw the text

    }

    private fun Canvas.drawText() {
        val textPositionX = width / 2
        val textPositionY = height / 2 - (paint.descent() + paint.ascent()) / 2
        when (buttonState) {
            ButtonState.Loading -> drawText("Loading...", textPositionX.toFloat(), textPositionY, paint)
            ButtonState.Completed -> drawText("Completed...", textPositionX.toFloat(), textPositionY, paint)
            ButtonState.Clicked -> drawText("Clicked...", textPositionX.toFloat(), textPositionY, paint)
        }


    }

    private fun Canvas.drawLoadingProgressBar() {
        Log.i("LoadingButton.drawLoadingProgressBar", "vRectStart , vRectEnd : $vRectStart $vRectEnd")
        when (buttonState) {
            else -> {
                drawRect(vRectStart, 0f, vRectEnd, height.toFloat(), testPaint)
            }
        }
    }

    private fun Canvas.drawBackGroundColor() {
        drawColor(downloadCompletedColor)
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        vAnimator.cancel()
        vAnimator.removeAllUpdateListeners()

    }
}