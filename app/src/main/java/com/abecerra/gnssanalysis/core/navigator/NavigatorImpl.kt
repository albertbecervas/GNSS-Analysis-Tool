package com.abecerra.gnssanalysis.core.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.jetbrains.anko.startActivity

class NavigatorImpl(private var context: Context) : Navigator {




    override fun sendEmail(to: String) {
        with(context) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:$to")
            startActivity(emailIntent)
        }
    }

}