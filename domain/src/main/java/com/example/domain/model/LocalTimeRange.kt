package com.example.domain.model

import java.time.LocalTime

data class LocalTimeRange(
    val start: LocalTime,
    val end: LocalTime,
) {
    operator fun contains(time: LocalTime): Boolean = time in start..end
}
