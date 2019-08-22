package com.abecerra.gnssanalysis.core.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessScrollListener(private val loadMore: (Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
    private var currentPage = 0
    private var previousItem = 0

    override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
        val linearLayoutManager = recyclerView.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
        val totalItems = linearLayoutManager?.itemCount ?: 0
        val lastVisibleItem = linearLayoutManager?.findLastVisibleItemPosition() ?: 0

        when (newState) {
            androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE, androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING -> {
                if (lastVisibleItem == 1) {
                    currentPage = 0
                }
            }

            androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING -> {
                if (lastVisibleItem == totalItems - 1 && previousItem != lastVisibleItem) {
                    previousItem = lastVisibleItem
                    currentPage = currentPage.inc()
                    loadMore(currentPage)
                }
            }
        }
    }
}