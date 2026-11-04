package com.example.kafa.domain.model

enum class Frequency(val days: Int) {
    DAILY(1),
    WEEKLY(7),
    MONTHLY(30),
    QUARTERLY(90),
    YEARLY(365)
}
