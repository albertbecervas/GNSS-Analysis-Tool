package com.abecerra.pvt.computation.utils

import org.nield.kotlinstatistics.median
import org.nield.kotlinstatistics.standardDeviation
import kotlin.math.abs

fun outliers(pr: ArrayList<Double>): ArrayList<Int> {
    val indices = arrayListOf<Int>()

    //At least one imput argument is necessary
    if (pr.isEmpty()) return arrayListOf()

    //number of outliers that should be removed from the input vector/matrix
    var nOutliers = pr.size - 1

    //copy of the vector
    var x = pr.toMutableList()
    //Determine the number of samples in datain
    val n1 = x.size
    //There must be at least 3 samples.
    if (n1 < 3) return arrayListOf()
    //Sort the input data vector so that removing the extreme values becomes an
    //arbitrary task. Store indexes too, to be able to recreate the data to the
    //original order after removing the outliers. Also note that NaNs are
    //considered maximum by sort function:
    val idx = arrayListOf<Int>()
    val pairList = arrayListOf<Pair<Int, Double>>()
    x.forEachIndexed { index, d ->
        pairList.add(Pair(index, d))
    }
    pairList.sortBy {
        it.second
    }
    pairList.forEach {
        idx.add(it.first)
    }
    //Remove all Nans
    x.map { if (it.isNaN()) 0.0 else it }
    //size of the vector after NaNs removed
    var n = x.size
    //NaN elements gathered at the end by sort with default mode
    val nns = n1 - n
    //calculate stDev, standard deviation of input vector
    var stDev = x.standardDeviation()
    //calculate the sample mean
    var xBar = x.median()

    //tau is a vector containing values for Thompson's Tau:
    val tau = arrayOf(
        1.150, 1.393, 1.572, 1.656, 1.711, 1.749, 1.777, 1.798, 1.815, 1.829,
        1.840, 1.849, 1.858, 1.865, 1.871, 1.876, 1.881, 1.885, 1.889, 1.893,
        1.896, 1.899, 1.902, 1.904, 1.906, 1.908, 1.910, 1.911, 1.913, 1.914,
        1.916, 1.917, 1.919, 1.920, 1.921, 1.922, 1.923, 1.924
    )
    //Determine the value of stDev times Tau
    var tauS = if (n > tau.size) {
        1.960 * stDev //For n > 40
    } else {
        tau[n] * stDev //For samples of size 3 < n < 40
    }

    //Compare the values of extreme high/low data points with tauS:
    var i = 0
    var oldFirst = 0
    var olLast = idx.size

    var ol: Double
    var beg0: Int
    var end0: Int
    while (nOutliers > 0) {

        if (abs(x[0] - xBar) > abs(x[x.size - 1] - xBar)) {
            ol = abs(x[0] - xBar)
            beg0 = 1
            end0 = x.size
            oldFirst += 1
        } else {
            ol = abs(x[x.size - 1] - xBar)
            beg0 = 0
            end0 = x.size - 1
            olLast -= 1
        }

        if (ol > tauS) {
            x = x.subList(beg0, end0)

            n = x.size
            if (beg0 == 1) {
                indices.add(i, idx[oldFirst])
            } else {
                indices.add(i, idx[olLast - nns])
            }
            pr[indices[i]] = Double.NaN
            i += 1

            //Determine the NEW value of S times tau
            stDev = x.standardDeviation()
            xBar = x.median()

            tauS = if (n > tau.size) {
                1.960 * stDev //For n > 40
            } else {
                tau[n] * stDev //For samples of size 3 < n < 40
            }

        }

        nOutliers -= 1

    }

    indices.sortDescending()
    return indices
}
