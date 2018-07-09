package com.github.alexxxdev.bannerview

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton

class BannerView (context: Context, attributeSet: AttributeSet)
    : FrameLayout(context, attributeSet) {

    private var recyclerView: RecyclerView
    private var buttonPrev: ImageButton
    private var buttonNext: ImageButton
    private var indicator: BannerIndicatorView

    private var bannerLayoutManager: BannerLayoutManager

    private var onSelectedBannerListener: OnSelectedBannerListener? = null

    init {
        View.inflate(context, R.layout.banner_view_layout, this)

        recyclerView = findViewById(R.id.recyclerView)
        buttonPrev = findViewById(R.id.button_prev)
        buttonNext = findViewById(R.id.button_next)
        indicator = findViewById(R.id.indicator)

        bannerLayoutManager = BannerLayoutManager(getContext(), recyclerView, OrientationHelper.HORIZONTAL)

        val a = context.theme.obtainStyledAttributes(attributeSet, R.styleable.BannerView, 0, 0)
        try {
            val showIndicator = a.getBoolean(R.styleable.BannerView_showIndicator, false)
            if(showIndicator){
                indicator.visibility = View.VISIBLE
            }

            val showNavsBtns = a.getBoolean(R.styleable.BannerView_showNavBtns, false)
            if(showNavsBtns){
                val drawableNextNavBtn = a.getResourceId(R.styleable.BannerView_nextNavBtn, -1)
                val drawablePrevNavBtn = a.getResourceId(R.styleable.BannerView_prevNavBtn, -1)
                if(drawableNextNavBtn!=-1) {
                    buttonNext.setImageDrawable(AppCompatResources.getDrawable(context, drawableNextNavBtn))
                }
                if(drawablePrevNavBtn!=-1) {
                    buttonPrev.setImageDrawable(AppCompatResources.getDrawable(context, drawablePrevNavBtn))
                }
                buttonPrev.visibility = View.VISIBLE
                buttonNext.visibility = View.VISIBLE

                buttonPrev.setOnClickListener { bannerLayoutManager.prev() }
                buttonNext.setOnClickListener { bannerLayoutManager.next() }
            }

            val indicatorMarginBottom = a.getDimensionPixelSize(R.styleable.BannerView_indicatorMarginBottom, -1)
            if(indicatorMarginBottom>=0){
                val marginLayoutParams = indicator.layoutParams as MarginLayoutParams
                marginLayoutParams.bottomMargin = indicatorMarginBottom
            }
        } finally {
            a.recycle()
        }
    }

    fun setAdapter(adapter: InfiniteAdapter<*,*>) {
        recyclerView.layoutManager = bannerLayoutManager
        recyclerView.adapter = adapter
        indicator.setAdapter(adapter)
        bannerLayoutManager.recyclerView.adapter

        bannerLayoutManager.setOnSelectedBannerListener(object : OnSelectedBannerListener{
            override fun onSelectedBanner(view: View, position: Int) {
                indicator.setCurrentActive(position)
                onSelectedBannerListener?.onSelectedBanner(view, position)
            }
        })
    }

    fun setOnSelectedBannerListener(listener:OnSelectedBannerListener){
        onSelectedBannerListener = listener
    }
}