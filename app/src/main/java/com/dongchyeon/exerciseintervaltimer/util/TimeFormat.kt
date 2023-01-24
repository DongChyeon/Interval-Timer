package com.dongchyeon.exerciseintervaltimer.util

import kotlin.math.roundToInt

fun getFormattedTime(value: Int): String {
    return String.format("%02d", value)
}

fun Int.toMillis(): Long {
    return (this * 1000).toLong()
}

fun Long.toSec(): Int {
    return (this / 1000).toDouble().roundToInt()
}