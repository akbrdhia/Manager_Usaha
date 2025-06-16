package com.managerusaha.app.utills

import java.util.Calendar

fun getRangeBulanIni(): Pair<Long, Long> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis

    cal.add(Calendar.MONTH, 1)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val end = cal.timeInMillis - 1

    return start to end
}