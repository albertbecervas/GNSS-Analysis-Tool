package com.abecerra.gnssanalysis.presentation.ui.statistics

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_graphs_spinner.view.graphTitleTv

class GraphsTitleSpinnerAdapter(
    context: Context,
    layoutRes: Int,
    textViewId: Int,
    items: List<String>
) :
    ArrayAdapter<String>(context, layoutRes, textViewId, items) {


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        convertView?.graphTitleTv?.text = getItem(position)
        return super.getDropDownView(position, convertView, parent)
    }

}
