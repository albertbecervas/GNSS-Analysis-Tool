package com.abecerra.gnssanalysis.presentation.ui.modes

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.app.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.app.utils.extensions.context
import com.abecerra.pvt_computation.data.input.ComputationSettings
import org.jetbrains.anko.toast

class ComputationSettingsAdapter(private val onComputationSettingsSelected: () -> Unit) :
    RecyclerView.Adapter<ComputationSettingsViewHolder>() {

    private val mPrefs = AppSharedPreferences.getInstance()
    private var computationSettingsList = mPrefs.getComputationSettingsList()

    override fun getItemCount(): Int {
        return computationSettingsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComputationSettingsViewHolder {
        return ComputationSettingsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_computation_setting_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ComputationSettingsViewHolder, position: Int) {
        val computationSettings = computationSettingsList[position]

        holder.computationSettingsName.text = computationSettings.name
        setDescription(holder, computationSettings)
        initModeSelectedState(holder, computationSettings.isSelected)

        holder.itemView.setOnLongClickListener {
            holder.deleteButton.visibility = VISIBLE
            true
        }

        holder.itemView.setOnClickListener {
            if (holder.deleteButton.visibility == VISIBLE) {
                holder.deleteButton.visibility = GONE
            } else {
                if (setSelected(holder, computationSettings.isSelected)) {
                    computationSettingsList[position].isSelected = !computationSettingsList[position].isSelected
                    onComputationSettingsSelected.invoke()
                }
            }
        }

        holder.deleteButton.setOnClickListener {
            holder.deleteButton.visibility = GONE
//            toast("Mode '" + computationSettingsList[position].name + "' deleted")
            mPrefs.deleteComputationSettings(computationSettings)
            if (computationSettings.isSelected) {
                onComputationSettingsSelected.invoke()
            }
            computationSettingsList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    private fun initModeSelectedState(holder: ComputationSettingsViewHolder, selected: Boolean) {
        if (selected) {
            holder.computationSettingsName.setTextColor(getColor(context, R.color.black))
            holder.cvMode.elevation = 16f
            holder.checkImage.visibility = VISIBLE
            holder.cvMode.setCardBackgroundColor(getColor(context, R.color.colorAccent))
        } else {
            holder.computationSettingsName.setTextColor(getColor(context, R.color.gray))
            holder.cvMode.elevation = 0f
            holder.cvMode.setCardBackgroundColor(getColor(context, R.color.white))
            holder.checkImage.visibility = GONE
        }
    }

    private fun setSelected(holder: ComputationSettingsViewHolder, selected: Boolean): Boolean {
        val couldSelect: Boolean

        if (!selected) {
            val selectedModes = getSelectedItems()

            if (selectedModes.size < 5) {
                holder.computationSettingsName.setTextColor(getColor(context, R.color.black))
                holder.cvMode.elevation = 16f
                holder.checkImage.visibility = VISIBLE
                holder.cvMode.setCardBackgroundColor(getColor(context, R.color.colorAccent))
                couldSelect = true
            } else {
                holder.checkImage.context.toast("You can not select more than five computationSettingsList")
                couldSelect = false
            }
        } else {
            holder.computationSettingsName.setTextColor(getColor(context, R.color.gray))
            holder.cvMode.elevation = 0f
            holder.cvMode.setCardBackgroundColor(getColor(context, R.color.white))
            holder.checkImage.visibility = GONE
            couldSelect = true
        }

        return couldSelect
    }

    private fun setDescription(holder: ComputationSettingsViewHolder, mode: ComputationSettings) {
        holder.constellationsDescription.text = mode.constellationsAsString()
        holder.bandsDescription.text = mode.bandsAsString()
        holder.correctionsDescription.text = mode.correctionsAsString()
        holder.algorithmDescription.text = mode.algorithmAsString()
    }

    fun update() {
        computationSettingsList = mPrefs.getComputationSettingsList()
        this.notifyDataSetChanged()
    }

    fun getItems(): List<ComputationSettings> {
        return computationSettingsList
    }

    fun getSelectedItems(): List<ComputationSettings> {
        val selectedModes = computationSettingsList.filter {
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
