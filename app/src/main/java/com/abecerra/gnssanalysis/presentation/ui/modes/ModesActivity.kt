package com.abecerra.gnssanalysis.presentation.ui.modes

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseActivity
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.abecerra.pvt.computation.data.ComputationSettings
import com.abecerra.pvt.computation.data.PositionParameters
import kotlinx.android.synthetic.main.activity_modes.*
import kotlinx.android.synthetic.main.dialog_new_mode.view.*
import org.jetbrains.anko.toast

class ModesActivity : BaseActivity() {

    private var mAdapter: ModesAdapter? = null

    private var avg: Int = 5
    private var mask: Int = 5
    private var cnoMask: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modes)

        setViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_modes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.reset -> {
                    mPrefs.saveModes(addDefaultModes())
                    tvSelectedModesTitle.text = ""
                    mAdapter?.update()
                }
                else -> {
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.positioning_settings)

        mAdapter = ModesAdapter { setSelectedModes() }
        modesRVList.layoutManager = LinearLayoutManager(this)
        modesRVList.adapter = mAdapter

        setSelectedModes()

        fabNewMode.setOnClickListener {
            showNewModeDialog()
        }

        apply_gnss_modes.setOnClickListener {
            mAdapter?.let {
                val modes = it.getItems()
                var selectedModes = 0
                modes.forEachIndexed { index, mode ->
                    if (mode.isSelected && selectedModes < 5) {
                        mode.color = selectedModes
                        selectedModes++
                    }
                }
                mPrefs.saveModes(modes)
            }
//            mPrefs.setGnssLoggingEnabled(switchGnssLogs.isChecked)
//            mPrefs.setAverageEnabled(switchAvg.isChecked)
//            mPrefs.setAverage(avg)
//            mPrefs.setSelectedMask(mask)
//            mPrefs.setSelectedCnoMask(cnoMask)
            finish()
        }

//        avg = mPrefs.getAverage()
        seekBarTime.progress = avg
        val avgText = "$avg s"
        tvAvgValue.text = avgText

        mask = mPrefs.getSelectedMask()
        seekBarMask.progress = mask
        val maskText = "${mask}º"
        tvMaskValue.text = maskText

//        cnoMask = mPrefs.getSelectedCnoMask()
        seekBarCno.progress = cnoMask
        val cnoMaskText = "$cnoMask dB-Hz"
        tvCnoValue.text = cnoMaskText

//        switchAvg.isChecked = mPrefs.isAverageEnabled()
//        switchGnssLogs.isChecked = mPrefs.isGnssLoggingEnabled()
//        clAvgValue.visibility = if (mPrefs.isAverageEnabled()) VISIBLE else GONE

        switchAvg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                clAvgValue.visibility = VISIBLE
            } else {
                clAvgValue.visibility = GONE
            }
        }

        tvModesTitle.setOnClickListener {
            if (modesRVList.visibility == VISIBLE) {
                modesRVList.visibility = GONE
            } else {
                modesRVList.visibility = VISIBLE
            }
            ivModesTitle.rotation = ivModesTitle.rotation + 180f
        }

        clMask.setOnClickListener {
            if (clMaskValue.visibility == VISIBLE) {
                clMaskValue.visibility = GONE
            } else {
                clMaskValue.visibility = VISIBLE
            }
            ivMaskTitle.rotation = ivMaskTitle.rotation + 180f
        }

        clCno.setOnClickListener {
            if (clCnoValue.visibility == VISIBLE) {
                clCnoValue.visibility = GONE
            } else {
                clCnoValue.visibility = VISIBLE
            }
            ivCnoTitle.rotation = ivCnoTitle.rotation + 180f
        }

        seekBarTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                avg = progress
                val avgProgressText = "$progress s"
                tvAvgValue.text = avgProgressText

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        seekBarMask.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mask = progress
                val maskProgressText = "${mask}º"
                tvMaskValue.text = maskProgressText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        seekBarCno.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cnoMask = progress
                val cnoMaskProgressText = "$cnoMask dB-Hz"
                tvCnoValue.text = cnoMaskProgressText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setSelectedModes() {
        val selectedItems = mAdapter?.getSelectedItems() ?: arrayListOf()
        val selectedModesText = "${selectedItems.size} selected"
        tvSelectedModesTitle.text = selectedModesText
    }

    private fun showNewModeDialog() {

        val dialog = AlertDialog.Builder(this).create()
        val layout = View.inflate(this, R.layout.dialog_new_mode, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setView(layout)
        layout.createButton.setOnClickListener {
            createMode(layout, dialog)
        }

        layout?.let {
            // Ionosphere and iono-free can't be selected at the same time
            it.correctionsOption3.isEnabled = !it.correctionsOption1.isChecked
            it.correctionsOption1.isEnabled = !it.correctionsOption3.isChecked

            it.correctionsOption1.setOnClickListener { l ->
                it.correctionsOption3.isEnabled = !l.correctionsOption1.isChecked
            }
            it.correctionsOption3.setOnClickListener { l ->
                it.correctionsOption1.isEnabled = !l.correctionsOption3.isChecked
            }

            it.rgGpsBands.isEnabled = it.constOption1.isChecked
            it.rgGalBands.isEnabled = it.constOption2.isChecked

            it.constOption1.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    it.rbL1.isEnabled = true
                    it.rbL5.isEnabled = true
                    it.rbL1.isChecked = true
                } else {
                    it.rbL1.isEnabled = false
                    it.rbL5.isEnabled = false

                }
            }

            it.constOption2.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    it.rbE1.isEnabled = true
                    it.rbE5a.isEnabled = true
                    it.rbE1.isChecked = true
                } else {
                    it.rbE1.isEnabled = false
                    it.rbE5a.isEnabled = false
                }
            }


        }
        dialog.show()

    }

    private fun createMode(layout: View?, dialog: AlertDialog) {
        var name = ""
        val constellations = arrayListOf<Int>()
        val bands = arrayListOf<Int>()
        val corrections = arrayListOf<Int>()
        var algorithm = 0

        layout?.let {
            name = it.modeNameTextEdit.text.toString() // set the name
            if (it.constOption1.isChecked) constellations.add(PositionParameters.CONST_GPS) // set selected constellations
            if (it.constOption2.isChecked) constellations.add(PositionParameters.CONST_GAL)
            if (it.isEnabled && it.rbL1.isChecked) bands.add(PositionParameters.BAND_L1) // set selected bands
            if (it.isEnabled && it.rbL5.isChecked) bands.add(PositionParameters.BAND_L5)
            if (it.isEnabled && it.rbE1.isChecked) bands.add(PositionParameters.BAND_E1)
            if (it.isEnabled && it.rbE5a.isChecked) bands.add(PositionParameters.BAND_E5A)
            if (it.correctionsOption1.isChecked) corrections.add(PositionParameters.CORR_IONOSPHERE)  // set selected corrections
            if (it.correctionsOption2.isChecked) corrections.add(PositionParameters.CORR_TROPOSPHERE)
            if (it.correctionsOption3.isChecked) corrections.add(PositionParameters.CORR_IONOFREE)
            if (it.algorithm1.isChecked) algorithm = PositionParameters.ALG_LS  // set selected algorithm
            if (it.algorithm2.isChecked) algorithm = PositionParameters.ALG_WLS
        }

        val modesList = AppSharedPreferences.getInstance().getModesList()
        if (modeCanBeAdded(name, constellations, bands, modesList)) {
            val mode = ComputationSettings(
                modesList.size,
                name,
                constellations,
                bands,
                corrections,
                algorithm,
                isSelected = false
            )
            AppSharedPreferences.getInstance().saveMode(mode)
            toast("Mode created")
            dialog.dismiss()
            mAdapter?.update()
        }
    }

    private fun modeCanBeAdded(
        name: String,
        constellations: ArrayList<Int>,
        bands: ArrayList<Int>,
        modesList: ArrayList<ComputationSettings>
    ): Boolean {

        var canBeAdded = false

        if (name.isNotBlank()) {
            if (!modesList.any { mode -> mode.name == name }) { //if name is not repeated
                if (constellations.isNotEmpty()) { // if one constellation is selected
                    if (bands.isNotEmpty()) { //if one band is selected
                        canBeAdded = true
                    } else { //if no band is selected
                        toast("At least one band must be selected")
                    }
                } else { //if no constellation is selected
                    toast("At least one constellattion must be selected")
                }
            } else { //if name already exists
                toast("This name already exists")
            }
        } else {//if name is blank
            toast("Name can not be blank")
        }
        return canBeAdded
    }
}
