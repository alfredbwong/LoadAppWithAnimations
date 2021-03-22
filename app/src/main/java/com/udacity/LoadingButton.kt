package com.udacity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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

    private var vBackgroundAnimator = ValueAnimator()

    private val animatorSet: AnimatorSet = AnimatorSet().apply {
        duration = 3000L
    }

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private val testPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        isAntiAlias = true
    }
    private val testPaint2 = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
        isAntiAlias = true
    }

    private var vRectEnd = 0f
    private var vCircleEnd = 0f
    private val progressCircleRectF = RectF()
    private var progressCircleAnimator = ValueAnimator()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                progressBarAnimationCalc()
                progressCircleAnimationCalc()
            }
            else -> {
                //Reset background color and remove animator
                Log.i("buttonState.clear", " Clearing")
                progressCircleAnimator.cancel()
                vBackgroundAnimator.cancel()
                vCircleEnd = 0F
                vRectEnd = 0F
                invalidate()
            }
        }


    }

    private fun progressCircleAnimationCalc() {
        var hCenter = widthSize.toFloat() * 0.75F
        var vCenter = heightSize / 2.0F
        progressCircleAnimator = ValueAnimator.ofFloat(0f, 360f)
        progressCircleAnimator.addUpdateListener {
            vCircleEnd = it.animatedValue as Float
            invalidate()

        }

        progressCircleAnimator.duration = 2000L
        progressCircleAnimator.repeatMode = ValueAnimator.RESTART
        progressCircleAnimator.repeatCount = ValueAnimator.INFINITE
        progressCircleAnimator.start()


    }

    private fun progressBarAnimationCalc() {
        vBackgroundAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        vBackgroundAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            vRectEnd = value
            invalidate()
        }
        vBackgroundAnimator.duration = 3000L
        vBackgroundAnimator.repeatCount = ValueAnimator.INFINITE
        vBackgroundAnimator.repeatMode = ValueAnimator.RESTART
        vBackgroundAnimator.start()
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
            drawLoadingProgressCircle()
            drawText()
        }

    }

    private fun Canvas.drawLoadingProgressCircle() {
//        Log.i("LoadingButton.drawLoadingProgressCircle", "vCircleEnd : $vCircleEnd buttonState $buttonState progressCircleRectF $progressCircleRectF")
        drawArc(width.toFloat()*0.8F - 25F,height.toFloat()/2-25F,width.toFloat()*0.8F + 25F,height.toFloat()/2+25F, 0.0F, vCircleEnd, true, testPaint2)


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
//        Log.i("LoadingButton.drawLoadingProgressBar", "vRectStart , vRectEnd : $vRectStart $vRectEnd")
        when (buttonState) {
            else -> {
                drawRect(0f, 0f, vRectEnd, height.toFloat(), testPaint)
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
        if (buttonState == ButtonState.Completed){
            buttonState = ButtonState.Clicked
            invalidate()
        }

        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        vBackgroundAnimator.removeAllUpdateListeners()
        vBackgroundAnimator.cancel()

    }

    fun changeButtonState(state: ButtonState) {
        Log.i("LoadingButton", "Change button state : $state")
        if(buttonState != state){
            buttonState = state

        }
    }
}