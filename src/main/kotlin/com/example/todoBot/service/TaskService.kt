package com.example.todoBot.service

import com.example.todoBot.Utils.Utils.Companion.dateFormat
import com.example.todoBot.Utils.Utils.Companion.timeFormat
import com.example.todoBot.enums.Status
import com.example.todoBot.model.TaskModel
import com.example.todoBot.repository.TasksRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class TaskService(private val tasksRepository: TasksRepository) {

    fun create(message: List<String>): String {
        val deadlineDate = message[message.lastIndex - 1]
        val deadlineTime = message.last()
        val name = message.subList(1, message.lastIndex - 1).joinToString(separator = " ")
        if (!dateFormat.matches(deadlineDate)){
            return "Я не понимаю такой формат даты :P"
        }
        if (!timeFormat.matches(deadlineTime)) {
            return "Время указано неправильно, исправьте пожалуйста >.<"
        }
        val existingTask = tasksRepository.findByName(name)
        if (existingTask != null) {
            return "Зачем добавлять уже существующую задачу? (:"
        }
        val createdAt = ZonedDateTime.now()
        val deadline = getTimeFromString(deadlineDate, deadlineTime)
        val task = TaskModel(name = name, createdAt = createdAt, deadline = deadline, status = Status.TODO)
        tasksRepository.save(task)

        return "Задача \"${task.name}\" успешно добавлена!"
    }

    private fun getTimeFromString(date: String, time: String): ZonedDateTime {
        val localDate = LocalDate.parse(date)
        val localTime = LocalTime.parse(time)
        val zone = ZoneId.of("Europe/Moscow")

        return ZonedDateTime.of(localDate, localTime, zone)
    }
}