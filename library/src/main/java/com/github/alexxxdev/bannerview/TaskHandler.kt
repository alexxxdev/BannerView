package com.github.alexxxdev.bannerview

import android.os.Handler
import android.os.Message
import android.util.Log
import java.lang.ref.WeakReference

internal class TaskHandler(bannerLayoutManager: BannerLayoutManager): Handler() {
    private val weakBanner: WeakReference<BannerLayoutManager> = WeakReference(bannerLayoutManager)
    var isSendPositionMessage: Boolean = false

    override fun handleMessage(msg: Message?) {
        super.handleMessage(msg)

        if (msg != null && isSendPositionMessage) {
            val position = msg.obj as Int
            weakBanner.get()?.let {
                it.recyclerView.smoothScrollToPosition(position)
            }
        }
    }
}