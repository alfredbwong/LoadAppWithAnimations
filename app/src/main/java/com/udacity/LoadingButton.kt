package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0


    private var downloadCircleColor = 0
    private var downloadLoadingColor = 0
    private var downloadCompletedColor = 0

    private var vBackgroundAnimator = ValueAnimator()

    private var buttonText = ""

    var isSelectionValid = false

    private val animatorSet = AnimatorSet().apply {
        duration = 1000L
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                this@LoadingButton.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                this@LoadingButton.isEnabled = true
            }
        })

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
        Log.i("LoadingButton.buttonState", "old : $old  new : $new")
        when (new) {
            ButtonState.Loading -> {
                progressBarAnimationCalc()
                progressCircleAnimationCalc()
                buttonText = "Loading..."
                animatorSet.playTogether(progressCircleAnimator, vBackgroundAnimator)
                animatorSet.start()
            }
            ButtonState.Clicked -> {
                if (!isSelectionValid) {
                    changeButtonState(ButtonState.Completed)
                }
            }
            else -> {
                //Reset background color and remove animator
                Log.i("LoadingButton", "not loading anymore invalidate $buttonState")
                animatorSet.cancel()
                vCircleEnd = 0F
                vRectEnd = 0F

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

        progressCircleAnimator.duration = 3000L
        if (isSelectionValid) {
            progressCircleAnimator.repeatMode = ValueAnimator.RESTART
            progressCircleAnimator.repeatCount = ValueAnimator.INFINITE
        }



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
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            downloadCircleColor = getColor(R.styleable.LoadingButton_downloadCircleColor, 0)
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
        testPaint2.apply{
            color = downloadCircleColor
        }
        drawArc(width.toFloat() * 0.8F - 25F
                , height.toFloat() / 2 - 25F,
                width.toFloat() * 0.8F + 25F
                , height.toFloat() / 2 + 25F,
                0.0F, vCircleEnd,
                true,
                testPaint2)


    }

    private fun Canvas.drawText() {
//        Log.i("LoadingButton.drawText", "buttonState: $buttonState")
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
        testPaint.apply {
            color = downloadLoadingColor
        }
        drawRect(0f, 0f, vRectEnd, height.toFloat(), testPaint)
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
        Log.i("LoadingButton.performClick", "$buttonState")
        super.performClick()
        if (buttonState == ButtonState.Completed) {
            buttonState = ButtonState.Clicked
            invalidate()
        }

        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorSet.cancel()

    }

    fun changeButtonState(state: ButtonState) {
        Log.i("LoadingButton.changeButtonState", "Change button state to: $state from $buttonState")
        if (buttonState != state) {
            buttonState = state
            invalidate()
        }

    }
}