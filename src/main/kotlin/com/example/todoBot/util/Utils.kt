package com.example.todoBot.util

import com.example.todoBot.enums.Freq
import com.example.todoBot.model.TaskModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class Utils {

    companion object {
        val dateFormat = Regex("""^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12]\d|3[01])$""")
        val timeFormat = Regex("""^([0-1]?\d|2[0-3]):[0-5]\d$""")
        val statusFormat = Regex("""TODO|DONE|DELAYED""")
        val freqFormat = Regex("""DAILY|WEEKLY|MONTHLY|ONCE""")
        private const val ONE_DAY = 24 * 60 * 60

        fun getTimeFromString(date: String, time: String): ZonedDateTime {
            val localDate = LocalDate.parse(date)
            val localTime = LocalTime.parse(time)
            val zone = ZoneId.of("Europe/Moscow")

            return ZonedDateTime.of(localDate, localTime, zone)
        }

        fun getStringFromTaskModel(taskModel: TaskModel): String {
            val name = taskModel.name
            val deadlineDate = taskModel.deadline?.toLocalDate()
            val deadlineTime = taskModel.deadline?.toLocalTime()
            val status = taskModel.status

            return "*Название:* $name,  *срок:* $deadlineDate $deadlineTime,  *статус:* $status\n"
        }

        fun getInitialDelay(time: LocalTime, date: LocalDate): Long {
            val currentTime = ZonedDateTime.now()
            val zone = ZoneId.systemDefault()
            val scheduledTime = ZonedDateTime.of(date, time, zone)

            return ChronoUnit.SECONDS.between(currentTime, scheduledTime)
        }

        fun getPeriod(freq: Freq): Long {
            val day = ONE_DAY
            val month = 30 * day
            val week = 7 * day
            val period = when (freq) {
                Freq.DAILY -> day //10
                Freq.MONTHLY -> month
                Freq.WEEKLY -> week
                else -> 0
            }

            return period.toLong()
        }
    }
}