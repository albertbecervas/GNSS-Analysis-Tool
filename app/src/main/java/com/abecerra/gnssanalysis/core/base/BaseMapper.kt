package com.abecerra.gnssanalysis.core.base

abstract class BaseMapper<in From, out To> {

    abstract fun map(from: From): To

    open fun map(from: Collection<From>) = from.map { map(it) }
}
