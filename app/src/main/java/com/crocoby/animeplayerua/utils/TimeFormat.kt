package com.crocoby.animeplayerua.utils

fun msToTimeString(ms: Int): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    val dSeconds = seconds % 60
    val dMinutes = minutes % 60

    val sSeconds = dSeconds.toString()
    val sMinutes = dMinutes.toString()
    val sHours = hours.toString()

    return if (hours > 0) {
        "$sHours:${"0".repeat(2-sMinutes.length)}${sMinutes}:${"0".repeat(2-sSeconds.length)}${sSeconds}"
    } else {
        "${"0".repeat(2-sMinutes.length)}${sMinutes}:${"0".repeat(2-sSeconds.length)}${sSeconds}"
    }
}