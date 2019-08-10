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
        const val MODES: String = "modes"
        const val MASK: String = "mask"


        private var INSTANCE: AppSharedPreferences? = null

        fun getInstance(): AppSharedPreferences {
            if (INSTANCE == null) INSTANCE = AppSharedPreferences()
            return INSTANCE!!
        }
    }

    fun getSelectedMapType(): Int = mPrefs.getInt(SELECTED_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL)
    fun setSelectedMapType(type: Int) {
        mPrefs.edit()
            .putInt(SELECTED_MAP_TYPE, type)
            .apply()
    }

    fun getModesList(): ArrayList<ComputationSettings> {
        val gson = Gson()
        val type = object : TypeToken<List<ComputationSettings>>() {}.type

        val json = mPrefs.getString(MODES, "")

        return json?.let {
            if (it.isNotEmpty()) gson.fromJson<ArrayList<ComputationSettings>>(json, type)
            else arrayListOf()
        } ?: kotlin.run {
            arrayListOf<ComputationSettings>()
        }
    }

    fun getSelectedModesList(): List<ComputationSettings> {
        return getModesList().filter { it.isSelected }
    }

    fun saveMode(computationSettings: ComputationSettings) {
        val gson = Gson()
        val modesList = getModesList()
        modesList.add(computationSettings)

        val json = gson.toJson(modesList)
        mPrefs.edit()
            .putString(MODES, json)
            .apply()
    }

    fun saveModes(modes: List<ComputationSettings>) {
        val gson = Gson()
        val json = gson.toJson(modes)
        mPrefs.edit()
            .putString(MODES, json)
            .apply()
    }

    fun deleteMode(computationSettings: ComputationSettings): ArrayList<ComputationSettings> {
        val gson = Gson()
        val modesList = getModesList()
        modesList.remove(computationSettings)

        val json = gson.toJson(modesList)
        mPrefs.edit()
            .remove(MODES)
            .apply()

        mPrefs.edit()
            .putString(MODES, json)
            .apply()

        return modesList
    }

    fun getSelectedMask(): Int = mPrefs.getInt(MASK, 15)
    fun setSelectedMask(mask: Int) {
        mPrefs.edit()
            .putInt(MASK, mask)
            .apply()
    }
}