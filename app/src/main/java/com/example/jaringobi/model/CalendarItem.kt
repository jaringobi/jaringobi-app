package com.example.jaringobi.model

sealed class CalendarItem {
    data class Weekday(val name: String) : CalendarItem()

    data class Date(val day: Int) : CalendarItem()
}
