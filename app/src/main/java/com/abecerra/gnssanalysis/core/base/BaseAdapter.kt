package com.abecerra.gnssanalysis.core.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<K : androidx.recyclerview.widget.RecyclerView.ViewHolder, L> : androidx.recyclerview.widget.RecyclerView.Adapter<K>() {

    private val mItems: ArrayList<L> = arrayListOf()

    override fun onBindViewHolder(p0: K, p1: Int) {
        onBindViewHolder(p0, mItems[p1], p1)
    }

    abstract fun onBindViewHolder(holder: K, item: L, position: Int = -1)

    override fun getItemCount(): Int = mItems.size

    fun getItems() = mItems

    fun setItems(items: List<L>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun replaceItem(oldItem: L, newItem: L) {
        val oldItemPosition = mItems.indexOf(oldItem)
        mItems.removeAt(oldItemPosition)
        mItems.add(oldItemPosition, newItem)
        notifyDataSetChanged()
    }

    fun addItems(items: List<L>) {
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: L) {
        mItems.add(item)
        notifyDataSetChanged()
    }

    fun updateItem(item: L) {
        val indexOfItem = mItems.indexOf(item)
        mItems.removeAt(indexOfItem)
        mItems.add(indexOfItem, item)
        notifyItemChanged(indexOfItem)
    }

    fun remove(center: L) {
        mItems.remove(center)
        notifyDataSetChanged()
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun clear(predicate: (L) -> Boolean) {
        val newList = mItems.filter(predicate)
        mItems.clear()
        mItems.addAll(newList)
        notifyDataSetChanged()
    }

}