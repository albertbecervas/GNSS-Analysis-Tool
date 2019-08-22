package com.abecerra.gnssanalysis.presentation.ui.modes

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_mode_list.view.*

class ModesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val modeName: TextView = view.itemTitle
    val constellationsDescription: TextView = view.constellationsDescription
    val bandsDescription: TextView = view.bandsDescription
    val correctionsDescription: TextView = view.correctionsDescription
    val algorithmDescription: TextView = view.algorithmDescription
    val deleteButton: ImageButton = view.deleteButton
    val checkImage: ImageView = view.ivChecked
    val cvMode: CardView = view.cvMode
}