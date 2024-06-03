package com.example.jaringobi.data.model

sealed class CalendarItem {
    data class Weekday(val name: String) : CalendarItem()

    data class Date(val day: Int) : CalendarItem()

    object Empty : CalendarItem()
}
