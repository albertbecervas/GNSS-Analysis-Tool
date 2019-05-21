package com.abecerra.gnssanalysis.core.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class EndlessScrollListener(private val loadMore: (Int) -> Unit) : RecyclerView.OnScrollListener() {
    private var currentPage = 0
    private var previousItem = 0

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager
        val totalItems = linearLayoutManager?.itemCount ?: 0
        val lastVisibleItem = linearLayoutManager?.findLastVisibleItemPosition() ?: 0

        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE, RecyclerView.SCROLL_STATE_DRAGGING -> {
                if (lastVisibleItem == 1) {
                    currentPage = 0
                }
            }

            RecyclerView.SCROLL_STATE_SETTLING -> {
                if (lastVisibleItem == totalItems - 1 && previousItem != lastVisibleItem) {
                    previousItem = lastVisibleItem
                    currentPage = currentPage.inc()
                    loadMore(currentPage)
                }
            }
        }
    }
}