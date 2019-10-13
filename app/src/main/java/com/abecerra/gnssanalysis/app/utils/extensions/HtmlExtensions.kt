package com.abecerra.gnssanalysis.app.utils.extensions

import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned

fun fromHtmlCompat(html: String): Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            html.replace("<!--.*?-->", ""),
            Html.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(html.replace("<!--.*?-->", ""))
    }

fun fromHtmlCompat(html: Spanned): Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            html.toString().replace("<!--.*?-->", ""),
            Html.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(html.toString().replace("<!--.*?-->", ""))
    }

fun fromHtmlCompat(html: SpannableString): Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            html.toString().replace("<!--.*?-->", ""),
            Html.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(html.toString().replace("<!--.*?-->", ""))
    }
