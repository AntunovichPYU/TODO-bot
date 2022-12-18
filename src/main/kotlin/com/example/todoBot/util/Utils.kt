package com.example.todoBot.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class Utils {

    companion object {
        val dateFormat = Regex("""^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12]\d|3[01])$""")
        val timeFormat = Regex("""^([0-1]?\d|2[0-3]):[0-5]\d$""")
        val statusFormat = Regex("""TODO|DONE|DELAYED""")

        fun getTimeFromString(date: String, time: String): ZonedDateTime {
            val localDate = LocalDate.parse(date)
            val localTime = LocalTime.parse(time)
            val zone = ZoneId.of("Europe/Moscow")

            return ZonedDateTime.of(localDate, localTime, zone)
        }
    }
}