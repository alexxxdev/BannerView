package com.github.alexxxdev.bannerview

import android.content.Context
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics

class BannerLayoutManager @JvmOverloads constructor(context: Context, val recyclerView: RecyclerView, orientation: Int = RecyclerView.VERTICAL, reverse: Boolean = false)
    : LinearLayoutManager(context, orientation, reverse) {

    private val MESSAGE_ID = 127
    private val START_POS = 1
    private var currentPosition = START_POS
    private var timeSmooth = 100L
    private var timeDelayed = 4000L

    private var linearSnapHelper: LinearSnapHelper = LinearSnapHelper()
    private var mHandler: TaskHandler = TaskHandler(this)

    private var onSelectedBannerListener: OnSelectedBannerListener? = null

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        linearSnapHelper.attachToRecyclerView(view)
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        super.onAdapterChanged(oldAdapter, newAdapter)

        currentPosition = START_POS
        mHandler.removeMessages(MESSAGE_ID)
        recyclerView.scrollToPosition(currentPosition)

        recyclerView.post {
            linearSnapHelper.findSnapView(this)?.let {
                if (onSelectedBannerListener != null)
                    onSelectedBannerListener?.onSelectedBanner(it, (currentPosition % itemCount) - 1)
            }
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView?.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return timeSmooth.toFloat() / displayMetrics.densityDpi
            }
        }

        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                linearSnapHelper.findSnapView(this)?.let {
                    currentPosition = getPosition(it)
                    var hasSendMessage = true
                    if (currentPosition > 0 && currentPosition % (itemCount - 1) == 0) {
                        currentPosition = START_POS
                        scrollToPosition(currentPosition)
                        hasSendMessage = false
                    } else if (currentPosition == 0) {
                        currentPosition = itemCount - 2
                        scrollToPosition(itemCount - 2)
                        hasSendMessage = false
                    }

                    if (onSelectedBannerListener != null)
                        onSelectedBannerListener?.onSelectedBanner(it, (currentPosition % itemCount) - 1)

                    if(hasSendMessage) sendMessage { currentPosition++ }
                }
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                mHandler.isSendPositionMessage = false
                mHandler.removeMessages(MESSAGE_ID)
                linearSnapHelper.findSnapView(this)?.let {
                    currentPosition = getPosition(it)
                    if (currentPosition > 0 && currentPosition % (itemCount - 1) == 0) {
                        currentPosition = START_POS
                        scrollToPosition(currentPosition)
                        return
                    } else if (currentPosition == 0) {
                        currentPosition = itemCount - 2
                        scrollToPosition(itemCount - 2)
                        return
                    }
                }
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {

            }
        }
    }

    private fun sendMessage(withDelay: Boolean = true, function: () -> Unit) {
        mHandler.isSendPositionMessage = true
        function()
        val msg = createMessage(currentPosition)
        mHandler.removeMessages(MESSAGE_ID)
        if (withDelay)
            mHandler.sendMessageDelayed(msg, timeDelayed)
        else
            mHandler.sendMessage(msg)
    }

    private fun createMessage(position: Int): Message? {
        val msg = Message.obtain()
        msg.what = MESSAGE_ID
        msg.obj = position
        return msg
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        mHandler.isSendPositionMessage = true
        if (currentPosition + 1 >= itemCount) {
            return
        }
        val msg = createMessage(currentPosition + 1)
        mHandler.removeMessages(MESSAGE_ID)
        mHandler.sendMessageDelayed(msg, timeDelayed)
    }

    fun setTime(delayed: Long = timeDelayed, smooth: Long = timeSmooth) {
        timeDelayed = delayed
        timeSmooth = smooth
    }

    fun next() {
        linearSnapHelper.findSnapView(this)?.let {
            currentPosition = getPosition(it)
            if (currentPosition > 0 && currentPosition % (itemCount - 1) == 0) return
            sendMessage(false) { currentPosition++ }
        }
    }

    fun prev() {
        linearSnapHelper.findSnapView(this)?.let {
            currentPosition = getPosition(it)
            if (currentPosition == 0) return
            sendMessage(false) { currentPosition-- }
        }
    }

    fun setOnSelectedBannerListener(listener: OnSelectedBannerListener) {
        onSelectedBannerListener = listener
    }

}