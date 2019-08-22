package com.abecerra.gnssanalysis.core.base

import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.abecerra.gnssanalysis.core.utils.extensions.inflate

abstract class BasePagerAdapter<K>(@LayoutRes val layout: Int) : PagerAdapter() {

    val mItems: ArrayList<K> = arrayListOf()

    override fun getCount(): Int = mItems.size

    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 === p1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = inflate(layout, container)
        val item = mItems[position]

        bindItem(view, item)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as? View)
    }

    abstract fun bindItem(view: View, item: K)

    fun setItems(items: List<K>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun addItems(items: List<K>) {
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: K) {
        mItems.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

}