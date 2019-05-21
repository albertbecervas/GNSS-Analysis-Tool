package com.abecerra.gnssanalysis.core.base


abstract class BaseBidirectionalMapper<From, To> : BaseMapper<From, To>() {

    abstract fun inverseMap(from: To): From

    open fun inverseMap(from: Collection<To>): Collection<From> =
        from.map { inverseMap(it) }

}
