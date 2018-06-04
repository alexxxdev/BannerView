package com.github.alexxxdev.bannerview

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

internal class BannerIndicatorView(context: Context, attrs: AttributeSet) : View(context, attrs){

    private var pageCount: Int = 0
    private var currentActive = 0

    private var activeDrawable: Drawable? = null
    private var activeDrawableWidth = 0
    private var activeDrawableHeight = 0
    private var activeRadius = 0

    private var inactiveDrawable: Drawable? = null
    private var inActiveDrawableWidth = 0
    private var inActiveDrawableHeight = 0
    private var inactiveRadius = 0

    private var maxRadius = 0
    private var minRadius = 0
    private var deltaRadius = 0

    private var drawablePadding: Int = 0

    private var activePaint: Paint = Paint()
    private var inactivePaint: Paint = Paint()

    private var adapter: InfiniteAdapter<*,*>? = null

    private val dataSetObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            adapter?.let {
                setPageCount(it.itemCount)
            }
        }
    }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BannerIndicatorView, 0, 0)
        try {
            activeRadius = a.getDimensionPixelSize(R.styleable.BannerIndicatorView_activeRadius, 0)
            inactiveRadius = a.getDimensionPixelSize(R.styleable.BannerIndicatorView_inactiveRadius, 0)

            val inactiveColor = a.getColor(R.styleable.BannerIndicatorView_inactiveColor, 0)
            inactivePaint.color = inactiveColor
            inactivePaint.style = Paint.Style.FILL

            val activeColor = a.getColor(R.styleable.BannerIndicatorView_activeColor, 0)
            activePaint.color = activeColor
            activePaint.style = Paint.Style.FILL

            activeDrawable = a.getDrawable(R.styleable.BannerIndicatorView_activeDrawable)
            if(activeDrawable == null) activeDrawable = resources.getDrawable(R.drawable.indicator_active)
            activeDrawable?.let {
                activeDrawableWidth = it.intrinsicWidth
                activeDrawableHeight = it.intrinsicHeight
                if(activeDrawableWidth<=0){
                    activeDrawableWidth = activeRadius*2
                    activeDrawableHeight = activeRadius*2
                    activeRadius = 0
                }
            }

            inactiveDrawable = a.getDrawable(R.styleable.BannerIndicatorView_inactiveDrawable)
            if(inactiveDrawable == null) inactiveDrawable = resources.getDrawable(R.drawable.indicator_inactive)
            inactiveDrawable?.let {
                inActiveDrawableWidth = it.intrinsicWidth
                inActiveDrawableHeight = it.intrinsicHeight
                if(inActiveDrawableWidth<=0){
                    inActiveDrawableWidth = inactiveRadius*2
                    inActiveDrawableHeight = inactiveRadius*2
                    inactiveRadius = 0
                }
            }

            drawablePadding = a.getDimensionPixelSize(R.styleable.BannerIndicatorView_drawablePadding, 0)

            maxRadius = Math.max(activeRadius, inactiveRadius)
            minRadius = Math.min(activeRadius, inactiveRadius)
            deltaRadius = Math.abs(activeRadius - inactiveRadius)

        } finally {
            a.recycle()
        }
    }

    fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount - 2
        invalidate()
        requestLayout()
    }

    fun setCurrentActive(currentActive: Int) {
        this.currentActive = currentActive
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(measureSpec: Int): Int {
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        var resultWidth: Int = paddingLeft + paddingRight + (pageCount - 1) * drawablePadding

        if (specMode == View.MeasureSpec.EXACTLY) {
            return specSize
        } else {
            if (minRadius <= 0) {
                resultWidth += (pageCount - 1) * inActiveDrawableWidth + activeDrawableWidth
            } else {
                resultWidth += (2f * minRadius * pageCount + 2 * deltaRadius).toInt()
            }
            if (specMode == View.MeasureSpec.AT_MOST) {
                return Math.min(resultWidth, specSize)
            }
        }
        return resultWidth
    }

    private fun measureHeight(measureSpec: Int): Int {
        val result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        if (specMode == View.MeasureSpec.EXACTLY) {
            return specSize
        } else {
            if (maxRadius <= 0) {
                result = paddingTop + paddingBottom + Math.max(inActiveDrawableHeight, activeDrawableHeight)
            } else {
                result = (paddingTop + paddingBottom + 2 * maxRadius).toInt()
            }
            if (specMode == View.MeasureSpec.AT_MOST) {
                return Math.min(result, specSize)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var i: Int = 0

        if (pageCount > 1) {
            while (i < pageCount) {
                if (i == currentActive) {
                    drawActiveIndicator(i, paddingLeft, paddingTop, canvas)
                } else {
                    drawInactiveIndicator(i, paddingLeft, paddingTop, canvas)
                }
                i++
            }
        }
    }

    private fun drawInactiveIndicator(i: Int, paddingLeft: Int, paddingTop: Int, canvas: Canvas) {
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        if (minRadius <= 0) {
            left = paddingLeft + i * inActiveDrawableWidth + i * drawablePadding
            top = paddingTop
            right = left + inActiveDrawableWidth
            bottom = inActiveDrawableHeight + top
            inactiveDrawable?.setBounds(left, top, right, bottom)
            inactiveDrawable?.draw(canvas)
        } else {
            val cx = paddingLeft.toFloat() + minRadius + 2f * minRadius * i + drawablePadding * i + deltaRadius
            val cy = paddingTop + maxRadius
            canvas.drawCircle(cx, cy.toFloat(), inactiveRadius.toFloat(), inactivePaint)
        }
    }

    private fun drawActiveIndicator(i: Int, paddingLeft: Int, paddingTop: Int, canvas: Canvas) {
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        if (minRadius <= 0) {
            left = paddingLeft + i * activeDrawableWidth + i * drawablePadding
            top = paddingTop
            right = left + activeDrawableWidth
            bottom = activeDrawableHeight + top
            activeDrawable?.setBounds(left, top, right, bottom)
            activeDrawable?.draw(canvas)
        } else {
            val cx = paddingLeft.toFloat() + minRadius + 2f * minRadius * i + drawablePadding * i + deltaRadius
            val cy = paddingTop + maxRadius
            canvas.drawCircle(cx, cy.toFloat(), activeRadius.toFloat(), activePaint)
        }
    }

    fun setAdapter(adapter:InfiniteAdapter<*,*>) {
        releaseViewPager()
        this.adapter = adapter
        setPageCount(adapter.itemCount)
        adapter.registerAdapterDataObserver(dataSetObserver)
    }

    fun releaseViewPager() {
        adapter?.let {
            it.unregisterAdapterDataObserver(dataSetObserver)
            adapter = null
        }
    }
}
