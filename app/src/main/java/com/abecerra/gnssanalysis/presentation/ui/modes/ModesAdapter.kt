package com.abecerra.gnssanalysis.presentation.ui.modes

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.core.utils.extensions.context
import com.abecerra.pvt.computation.data.ComputationSettings
import org.jetbrains.anko.toast

class ModesAdapter(private val onModeSelected: () -> Unit) : RecyclerView.Adapter<ModesViewHolder>() {

    private val mPrefs = AppSharedPreferences.getInstance()
    private var modes = mPrefs.getModesList()

    override fun getItemCount(): Int {
        return modes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModesViewHolder {
        return ModesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mode_list, parent, false))
    }

    override fun onBindViewHolder(holder: ModesViewHolder, position: Int) {
        val mode = modes[position]

        holder.modeName.text = mode.name
        setDescription(holder, mode)
        initModeSelectedState(holder, mode.isSelected)

        holder.itemView.setOnLongClickListener {
            holder.deleteButton.visibility = VISIBLE
            true
        }

        holder.itemView.setOnClickListener {
            if (holder.deleteButton.visibility == VISIBLE) {
                holder.deleteButton.visibility = GONE
            } else {
                if (setSelected(holder, mode.isSelected)) {
                    modes[position].isSelected = !modes[position].isSelected
                    onModeSelected.invoke()
                }
            }
        }

        holder.deleteButton.setOnClickListener {
            holder.deleteButton.visibility = GONE
//            toast("Mode '" + modes[position].name + "' deleted")
            mPrefs.deleteMode(mode)
            if (mode.isSelected) {
                onModeSelected.invoke()
            }
            modes.removeAt(position)
            notifyDataSetChanged()
        }

    }

    private fun initModeSelectedState(holder: ModesViewHolder, selected: Boolean) {
        if (selected) {
            holder.modeName.setTextColor(getColor(context, R.color.black))
            holder.cvMode.elevation = 16f
            holder.checkImage.visibility = VISIBLE
            holder.cvMode.setCardBackgroundColor(getColor(context, R.color.colorAccent))
        } else {
            holder.modeName.setTextColor(getColor(context, R.color.gray))
            holder.cvMode.elevation = 0f
            holder.cvMode.setCardBackgroundColor(getColor(context, R.color.white))
            holder.checkImage.visibility = GONE
        }
    }

    private fun setSelected(holder: ModesViewHolder, selected: Boolean): Boolean {
        val couldSelect: Boolean

        if (!selected) {
            val selectedModes = getSelectedItems()

            if (selectedModes.size < 5) {
                holder.modeName.setTextColor(getColor(context, R.color.black))
                holder.cvMode.elevation = 16f
                holder.checkImage.visibility = VISIBLE
                holder.cvMode.setCardBackgroundColor(getColor(context, R.color.colorAccent))
                couldSelect = true
            } else {
                holder.checkImage.context.toast("You can not select more than five modes")
                couldSelect = false
            }
        } else {
            holder.modeName.setTextColor(getColor(context, R.color.gray))
            holder.cvMode.elevation = 0f
            holder.cvMode.setCardBackgroundColor(getColor(context, R.color.white))
            holder.checkImage.visibility = GONE
            couldSelect = true
        }

        return couldSelect
    }

    private fun setDescription(holder: ModesViewHolder, mode: ComputationSettings) {
        holder.constellationsDescription.text = mode.constellationsAsString()
        holder.bandsDescription.text = mode.bandsAsString()
        holder.correctionsDescription.text = mode.correctionsAsString()
        holder.algorithmDescription.text = mode.algorithmAsString()
    }

    fun update() {
        modes = mPrefs.getModesList()
        this.notifyDataSetChanged()
    }

    fun getItems(): List<ComputationSettings> {
        return modes
    }

    fun getSelectedItems(): List<ComputationSettings> {
        val selectedModes = modes.filter {
            it.isSelected
        }

        return if (selectedModes.size <= 5) {
            selectedModes
        } else {
            val fiveFirstModes = arrayListOf<ComputationSettings>()
            repeat(5) {
                fiveFirstModes.add(selectedModes[it])
            }
            fiveFirstModes
        }
    }
}