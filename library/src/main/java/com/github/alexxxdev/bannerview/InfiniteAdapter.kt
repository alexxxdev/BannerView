package com.github.alexxxdev.bannerview

import android.support.v7.widget.RecyclerView

abstract class InfiniteAdapter<VH : RecyclerView.ViewHolder, ITEM : ItemInfo>(itemList: List<ITEM>) : RecyclerView.Adapter<VH>() {

    protected val list: List<ITEM> = listOf(itemList.last()) + itemList + listOf(itemList.first())

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBind(holder, list[position % list.size], position)
    }

    abstract fun onBind(holder: VH, item: ITEM, position: Int)
}