package com.abecerra.gnssanalysis.core.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.abecerra.gnssanalysis.presentation.ui.main.MainActivity
import org.jetbrains.anko.intentFor

class NavigatorImpl(private var context: Context) : Navigator {


    override fun navigateToMainActivity() {
        startActivity<MainActivity>()
    }


    override fun sendEmail(to: String) {
        with(context) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:$to")
            startActivity(emailIntent)
        }
    }

    private inline fun <reified T : Activity> startActivity() {
        with(context) {
            startActivity(intentFor<T>())
        }
    }

}