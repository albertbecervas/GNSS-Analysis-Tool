package com.abecerra.gnssanalysis.app.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.Serializable

enum class DataState { LOADING, SUCCESS, ERROR }

data class Data<out T>(val dataState: DataState, val data: T? = null, val message: String? = null) :
    Serializable

fun <T> Single<T>.subscribe(
    onSubscribe: () -> Unit = {},
    success: (data: T) -> Unit,
    error: (throwable: Throwable) -> Unit,
    compositeDisposable: CompositeDisposable
) {

    subscribeOn(Schedulers.newThread())
        .doOnSubscribe { onSubscribe.invoke() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            success.invoke(it)
        }, {
            error.invoke(it)
        }).addToCompositeDisposable(compositeDisposable)
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(this, Observer(body))
}

fun <K> MutableLiveData<Data<K>>.showError(message: String?) {
    postValue(Data(dataState = DataState.ERROR, message = message))
}

fun <K> MutableLiveData<Data<K>>.showError(message: Int) {
    postValue(Data(dataState = DataState.ERROR, message = context.getString(message)))
}

fun <K> MutableLiveData<Data<K>>.showLoading() {
    postValue(Data(dataState = DataState.LOADING))
}

fun <K> MutableLiveData<Data<K>>.updateData(data: K?, message: String? = null) {
    postValue(
        Data(
            dataState = DataState.SUCCESS,
            data = data,
            message = message
        )
    )
}

fun <K> MutableLiveData<K>.updateData(data: K) {
    postValue(data)
}

//@Suppress("UNCHECKED_CAST")
//fun <K> MutableLiveData<Data<K>>.updateData(data: Result<K>, message: String? = null) {
//    postValue(
//        Data(
//            dataState = DataState.SUCCESS,
//            data = data.getOrElse { showError(it.message) } as K,
//            message = message
//        )
//    )
//}
