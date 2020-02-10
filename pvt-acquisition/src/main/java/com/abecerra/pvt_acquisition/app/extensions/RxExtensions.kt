package com.abecerra.pvt_acquisition.app.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addToCompositeDisposable(composite: CompositeDisposable) {
    composite.add(this)
}
