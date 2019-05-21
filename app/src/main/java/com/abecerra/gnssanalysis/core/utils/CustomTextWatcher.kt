package com.abecerra.gnssanalysis.core.utils

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

class CustomTextWatcher(private val disableDelay: Boolean = false, private val callback: (String) -> Unit) :
    TextWatcher {
    private var textToSend = ""
    private val handler = Handler()
    private val handlerRunnable = Runnable { callback(textToSend) }

    override fun afterTextChanged(s: Editable?) {
        if (disableDelay) {
            callback(textToSend)
        } else {
            handler.postDelayed(handlerRunnable, DELAY)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        handler.removeCallbacks(handlerRunnable)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.let {
            if (it.toString().isNotEmpty()) {
                textToSend = it.toString()
            }
        }
    }

    companion object {
        private const val DELAY = 700L
    }
}