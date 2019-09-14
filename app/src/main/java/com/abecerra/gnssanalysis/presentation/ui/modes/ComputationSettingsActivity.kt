package com.abecerra.gnssanalysis.presentation.ui.modes

import android.app.Activity
import android.app.AlertDialog
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
import com.abecerra.gnssanalysis.core.utils.extensions.showInfoDialog
import com.abecerra.gnssanalysis.core.utils.extensions.showNewModeDialog
import kotlinx.android.synthetic.main.activity_modes.*
import org.jetbrains.anko.toast

class ComputationSettingsActivity : BaseActivity() {

    private var mAdapter: ComputationSettingsAdapter? = null

    private var avg: Int = DEFAULT_AVG
    private var mask: Int = DEFAULT_ELEV
    private var cnoMask: Int = DEFAULT_CNO

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
                    mPrefs.saveComputationSettingsList(addDefaultComputationSettings())
                    setSelectedModes()
                    mAdapter?.update()
                }
                R.id.information -> {
                    showInfoDialog()
                }
                else -> {
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.computation_settings)

        mAdapter = ComputationSettingsAdapter { setSelectedModes() }
        modesRVList.layoutManager = LinearLayoutManager(this)
        modesRVList.adapter = mAdapter

        setSelectedModes()

        fabNewMode.setOnClickListener {
            showNewModeDialog { layout, dialog ->
                createComputationSettings(layout, dialog)
            }
        }

        apply_gnss_modes.setOnClickListener {
            saveSelectedSettings()
            setResult(Activity.RESULT_OK)
            finish()
        }

        avg = mPrefs.getAverage()
        seekBarTime.progress = avg
        val avgText = "$avg s"
        tvAvgValue.text = avgText

        mask = mPrefs.getSelectedMask()
        seekBarMask.progress = mask
        val maskText = "${mask}º"
        tvMaskValue.text = maskText

        cnoMask = mPrefs.getSelectedCnoMask()
        seekBarCno.progress = cnoMask
        val cnoMaskText = "$cnoMask dB-Hz"
        tvCnoValue.text = cnoMaskText

        switchAvg.isChecked = mPrefs.isAverageEnabled()
        switchGnssLogs.isChecked = mPrefs.isGnssLoggingEnabled()
        clAvgValue.visibility = if (mPrefs.isAverageEnabled()) VISIBLE else GONE

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

    private fun saveSelectedSettings() {
        mAdapter?.let {
            val computationSettings = it.getItems()
            var selectedCount = 0
            computationSettings.forEach { settings ->
                if (settings.isSelected && selectedCount < 5) {
                    settings.color = selectedCount
                    selectedCount++
                }
            }
            mPrefs.saveComputationSettingsList(computationSettings)
        }
        mPrefs.setGnssLoggingEnabled(switchGnssLogs.isChecked)
        mPrefs.setAverageEnabled(switchAvg.isChecked)
        mPrefs.setAverage(avg)
        mPrefs.setSelectedMask(mask)
        mPrefs.setSelectedCnoMask(cnoMask)
    }

    private fun setSelectedModes() {
        val selectedItems = mAdapter?.getSelectedItems() ?: arrayListOf()
        val selectedModesText = "${selectedItems.size} selected"
        tvSelectedModesTitle.text = selectedModesText
    }

    private fun createComputationSettings(layout: View?, dialog: AlertDialog) {

        setComputationSettingsSelectedParams(layout, dialog, this)?.let {
            AppSharedPreferences.getInstance().addComputationSettings(it)
            toast("Computation Settings created")
            dialog.dismiss()
            mAdapter?.update()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {
        const val DEFAULT_AVG: Int = 5
        const val DEFAULT_ELEV: Int = 5
        const val DEFAULT_CNO: Int = 0
    }
}
