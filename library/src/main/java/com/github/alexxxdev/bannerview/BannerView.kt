package com.github.alexxxdev.bannerview

import android.content.Context
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
                a.getDrawable(R.styleable.BannerView_nextNavBtn)?.let { buttonNext.setImageDrawable(it) }
                a.getDrawable(R.styleable.BannerView_prevNavBtn)?.let { buttonPrev.setImageDrawable(it) }

                buttonPrev.visibility = View.VISIBLE
                buttonNext.visibility = View.VISIBLE

                buttonPrev.setOnClickListener { bannerLayoutManager.prev() }
                buttonNext.setOnClickListener { bannerLayoutManager.next() }
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