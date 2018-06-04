package com.github.alexxxdev.bannerview

import android.view.View

interface OnSelectedBannerListener {
    fun onSelectedBanner(view: View, position: Int)
}