package com.example.todoBot.util

import com.example.todoBot.enums.Freq
import com.example.todoBot.enums.Status
import com.example.todoBot.model.TaskModel
import com.example.todoBot.util.Utils.Companion.getPeriod
import com.example.todoBot.util.Utils.Companion.getStringFromTaskModel
import com.example.todoBot.util.Utils.Companion.getTimeFromString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.ZonedDateTime

class UtilsTests {

    @Test
    fun getTimeFromString() {
        val dateString = "2022-12-03T10:15:00+03:00[Europe/Moscow]"
        val date = "2022-12-03"
        val time = "10:15"
        val expected = ZonedDateTime.parse(dateString)

        assertEquals(expected, getTimeFromString(date, time))
    }

    @Test
    fun getStringFromTaskModel() {
        val createdAt = ZonedDateTime.now()
        val deadlineTime = ZonedDateTime.now().plusDays(1)
        val task = TaskModel(1, createdAt, "simple task", deadlineTime, Status.DONE)
        val expected = "*Название:* simple task,  *срок:* ${deadlineTime.toLocalDate()} ${deadlineTime.toLocalTime()},  *статус:* DONE\n"

        assertEquals(expected, getStringFromTaskModel(task))
    }

    @Test
    fun getPeriod() {
        val daily = Freq.DAILY
        val weekly = Freq.WEEKLY
        val monthly = Freq.MONTHLY
        val day = 86400L
        val week = 604800L
        val month = 2592000L

        assertEquals(day, getPeriod(daily))
        assertEquals(week, getPeriod(weekly))
        assertEquals(month, getPeriod(monthly))
    }

}