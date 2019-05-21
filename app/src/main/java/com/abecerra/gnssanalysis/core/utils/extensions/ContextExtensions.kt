package com.abecerra.gnssanalysis.core.utils.extensions

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.provider.MediaStore
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import com.abecerra.gnssanalysis.core.App
import com.abecerra.gnssanalysis.core.base.BaseActivity

val Activity.app: App get() = application as App
val Fragment.app: App get() = activity?.application as App
val Service.app: App get() = application as App
val context: Context get() = App.getAppContext()

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.context, message, duration).show()
}

fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Activity.pickImageFromGallery() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    startActivityForResult(
        intent,
        BaseActivity.PICK_FROM_GALLERY
    )
}

fun Activity.pickImageFromCamera() {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    startActivityForResult(
        intent,
        BaseActivity.PICK_FROM_CAMERA
    )
}

fun enableFullScreen(window: Window) {
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun inflate(@LayoutRes resourceLayout: Int, viewGroup: ViewGroup? = null, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resourceLayout, viewGroup, attachToRoot)
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun dpToPx(dp: Int): Int = (dp / Resources.getSystem().displayMetrics.density).toInt()
fun pxToDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()