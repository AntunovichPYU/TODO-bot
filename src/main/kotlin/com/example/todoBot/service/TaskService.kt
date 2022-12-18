package com.example.todoBot.service

import com.example.todoBot.enums.Status
import com.example.todoBot.model.TaskModel
import com.example.todoBot.repository.TasksRepository
import com.example.todoBot.util.Utils.Companion.dateFormat
import com.example.todoBot.util.Utils.Companion.getTimeFromString
import com.example.todoBot.util.Utils.Companion.statusFormat
import com.example.todoBot.util.Utils.Companion.timeFormat
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class TaskService(private val tasksRepository: TasksRepository) {

    fun create(arguments: List<String>): String {
        val deadlineDate = arguments[arguments.lastIndex - 1]
        val deadlineTime = arguments.last()
        val name = arguments.subList(1, arguments.lastIndex - 1).joinToString(separator = " ")
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

    fun update(arguments: List<String>): String {
        val name = arguments.subList(1, arguments.lastIndex).joinToString(" ")
        val status = arguments.last()
        val existingTask = tasksRepository.findByName(name)
            ?: return  "Не получится изменить то, чего не существует("
        if (!statusFormat.matches(status)) {
            return "Пока что я не знаю таких состояний( Все, которые я знаю, перечислены здесь: /help"
        }
        val statusValue =  Status.valueOf(status)
        existingTask.status = statusValue
        tasksRepository.save(existingTask)

        return "Статус задачи успешно изменен!"
    }
}