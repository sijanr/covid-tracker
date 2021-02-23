package dev.sijanrijal.covidtracker.util

enum class Metric(val days : Int) {
    WEEK(7),
    MONTH(30),
    MAX(-1)
}

enum class CASE_TYPE {
    POSITIVE,
    NEGATIVE,
    DEATH,
}

enum class DATA_TYPE {
    STATE,
    US
}