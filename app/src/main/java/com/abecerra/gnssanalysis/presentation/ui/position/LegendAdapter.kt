package com.abecerra.gnssanalysis.presentation.ui.position

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseAdapter
import com.abecerra.gnssanalysis.core.utils.extensions.context
import com.abecerra.gnssanalysis.core.utils.extensions.inflate
import com.abecerra.gnssanalysis.core.utils.getLegendColor
import com.abecerra.pvt.computation.data.ComputationSettings
import kotlinx.android.synthetic.main.item_legend.view.*

class LegendAdapter : BaseAdapter<LegendAdapter.LegendViewHolder, ComputationSettings>() {

    override fun onBindViewHolder(holder: LegendViewHolder, item: ComputationSettings, position: Int) {
        holder.color.setCardBackgroundColor(ContextCompat.getColor(context, getLegendColor(item.color)))
        holder.mode.text = item.name
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LegendViewHolder {
        return LegendViewHolder(inflate(R.layout.item_legend, p0))
    }


    class LegendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val color: CardView = view.cvColor
        val mode: TextView = view.tvMode
    }
}