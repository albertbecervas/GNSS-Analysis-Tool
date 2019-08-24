package com.abecerra.gnssanalysis.core.utils

import android.content.SharedPreferences
import com.abecerra.gnssanalysis.core.App
import com.abecerra.pvt.computation.data.ComputationSettings
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppSharedPreferences {

    private val mPrefs: SharedPreferences = App.getAppContext().getSharedPreferences(MY_PREFS, 0)

    companion object {

        const val MY_PREFS: String = "MY_PREFS"
        const val SELECTED_MAP_TYPE = "selected_map_type"
        const val COMP_SETTINGS: String = "computation_settings"
        const val MASK: String = "mask"
        const val CN0_MASK: String = "cno_mask"
        const val AVG_ENABLED: String = "avgenabled"
        const val AVGTIME: String = "avgtime"
        const val TUTORIAL_SHOWN = "tutorial shown"
        const val SELECTED_GRAPH_TYPE = "selected_graph_type"
        const val GNSS_LOGGING_ENABLED = "gnss_logging_enabled"


        private var INSTANCE: AppSharedPreferences? = null

        fun getInstance(): AppSharedPreferences {
            if (INSTANCE == null) INSTANCE = AppSharedPreferences()
            return INSTANCE!!
        }
    }

    fun isTutorialShown() = mPrefs.getBoolean(TUTORIAL_SHOWN, false)
    fun setTutorialShown() {
        mPrefs.edit()
            .putBoolean(TUTORIAL_SHOWN, true)
            .apply()
    }

    fun getSelectedMapType(): Int = mPrefs.getInt(SELECTED_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL)
    fun setSelectedMapType(type: Int) {
        mPrefs.edit()
            .putInt(SELECTED_MAP_TYPE, type)
            .apply()
    }

    fun setSelectedGraphType(graphType: String) {
        mPrefs.edit()
            .putString(SELECTED_GRAPH_TYPE, graphType)
            .apply()
    }


    fun isGnssLoggingEnabled() = mPrefs.getBoolean(GNSS_LOGGING_ENABLED, false)
    fun setGnssLoggingEnabled(enabled: Boolean) {
        mPrefs.edit()
            .putBoolean(GNSS_LOGGING_ENABLED, enabled)
            .apply()
    }


    fun getComputationSettingsList(): ArrayList<ComputationSettings> {
        val gson = Gson()
        val type = object : TypeToken<List<ComputationSettings>>() {}.type

        val json = mPrefs.getString(COMP_SETTINGS, "")

        return json?.let {
            if (it.isNotEmpty()) gson.fromJson<ArrayList<ComputationSettings>>(json, type)
            else arrayListOf()
        } ?: kotlin.run {
            arrayListOf<ComputationSettings>()
        }
    }

    fun getSelectedComputationSettingsList(): List<ComputationSettings> {
        return getComputationSettingsList().filter { it.isSelected }
    }

    fun addComputationSettings(computationSettings: ComputationSettings) {
        val gson = Gson()
        val modesList = getComputationSettingsList()
        modesList.add(computationSettings)

        val json = gson.toJson(modesList)
        mPrefs.edit()
            .putString(COMP_SETTINGS, json)
            .apply()
    }

    fun saveComputationSettingsList(computationSettings: List<ComputationSettings>) {
        val gson = Gson()
        val json = gson.toJson(computationSettings)
        mPrefs.edit()
            .putString(COMP_SETTINGS, json)
            .apply()
    }

    fun deleteComputationSettings(computationSettings: ComputationSettings): ArrayList<ComputationSettings> {
        val gson = Gson()
        val modesList = getComputationSettingsList()
        modesList.remove(computationSettings)

        val json = gson.toJson(modesList)
        mPrefs.edit()
            .remove(COMP_SETTINGS)
            .apply()

        mPrefs.edit()
            .putString(COMP_SETTINGS, json)
            .apply()

        return modesList
    }


    fun isAverageEnabled(): Boolean = mPrefs.getBoolean(AVG_ENABLED, true)
    fun setAverageEnabled(enabled: Boolean) {
        mPrefs.edit()
            .putBoolean(AVG_ENABLED, enabled)
            .apply()
    }

    fun getAverage(): Int = mPrefs.getInt(AVGTIME, 5)
    fun setAverage(avg: Int) {
        mPrefs.edit()
            .putInt(AVGTIME, avg)
            .apply()
    }

    fun getSelectedMask(): Int = mPrefs.getInt(MASK, 15)
    fun setSelectedMask(mask: Int) {
        mPrefs.edit()
            .putInt(MASK, mask)
            .apply()
    }

    fun getSelectedCnoMask(): Int = mPrefs.getInt(CN0_MASK, 0)
    fun setSelectedCnoMask(mask: Int) {
        mPrefs.edit()
            .putInt(CN0_MASK, mask)
            .apply()
    }
}