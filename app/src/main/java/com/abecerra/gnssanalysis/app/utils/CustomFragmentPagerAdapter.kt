package com.abecerra.gnssanalysis.core.utils

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter

open class CustomFragmentPagerAdapter<T: Fragment>(fragmentManager: FragmentManager) :
    SmartFragmentStatePagerAdapter(fragmentManager) {

    protected val fragments = arrayListOf<T>()
    private val fragmentsName = arrayListOf<String>()

    fun addFragment(fragment: T, fragmentName: String = "") {
        this.fragments.add(fragment)
        this.fragmentsName.add(fragmentName)
    }

    open fun addFragments(fragments: List<T>) {
        this.fragments.clear()
        this.fragmentsName.clear()
        this.fragments.addAll(fragments)
    }

    override fun getItem(position: Int): T = fragments[position]

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position < fragmentsName.size) {
            fragmentsName[position]
        } else ""
    }

    override fun getCount(): Int = fragments.size

    override fun saveState(): Parcelable? = null
}
