package com.example.todoBot.service

import com.example.todoBot.enums.Freq
import com.example.todoBot.enums.Status
import com.example.todoBot.model.TaskModel
import com.example.todoBot.repository.TasksRepository
import com.example.todoBot.util.Utils.Companion.dateFormat
import com.example.todoBot.util.Utils.Companion.freqFormat
import com.example.todoBot.util.Utils.Companion.getInitialDelay
import com.example.todoBot.util.Utils.Companion.getPeriod
import com.example.todoBot.util.Utils.Companion.getStringFromTaskModel
import com.example.todoBot.util.Utils.Companion.getTimeFromString
import com.example.todoBot.util.Utils.Companion.statusFormat
import com.example.todoBot.util.Utils.Companion.timeFormat
import com.google.common.collect.Iterables
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class TaskService(private val tasksRepository: TasksRepository) {

    private var scheduler = Executors.newSingleThreadScheduledExecutor()
    private var deadlineChecker = Executors.newSingleThreadScheduledExecutor()


    fun create(arguments: List<String>): String {
        val deadlineDate = arguments[arguments.lastIndex - 1]
        val deadlineTime = arguments.last()
        val name = arguments.subList(1, arguments.lastIndex - 1).joinToString(separator = " ")
        if (name.length > 50) {
            return "Слишком длинное название! Не стоит так подробно описывать свои задачи."
        }
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

    fun delete(arguments: List<String>): String {
        val name = arguments.subList(1, arguments.size).joinToString(" ")
        val existingTask = tasksRepository.findByName(name)
            ?: return "Не получится удалить то, чего не существует("
        tasksRepository.delete(existingTask)

        return "Задача успешна удалена!"
    }

    fun getAll(): String {
        val tasks = tasksRepository.findAll()
        val sb = StringBuilder()
        if (Iterables.isEmpty(tasks)) {
            sb.append("Ой, здесь пусто UwU")
            return sb.toString()
        }
        for (task: TaskModel in tasks) {
            sb.append(getStringFromTaskModel(task))
        }

        return sb.toString()
    }

    fun setNotification(chatId: Long, sender: AbsSender, args: List<String>) : String {
        val defaultFreq = Freq.ONCE
        val defaultDate = LocalDate.now()
        val freq: Freq
        val time: LocalTime
        val date: LocalDate
        val lastArg = args.last()
        val secondToLast = args[args.lastIndex - 1]
        var returnedMessage = "Неправильно вы аргументы подбираете. Попробуйте /help"


        if (freqFormat.matches(lastArg)) {
            freq = Freq.valueOf(lastArg)

            if (timeFormat.matches(secondToLast)) {
                time = LocalTime.parse(secondToLast)
            } else {
                return returnedMessage
            }
            date = defaultDate
        } else {
            freq = defaultFreq

            if (timeFormat.matches(lastArg)) {
                time = LocalTime.parse(lastArg)

                date = if (dateFormat.matches(secondToLast)) LocalDate.parse(secondToLast)
                else {
                    return returnedMessage
                }


            } else {
                return returnedMessage
            }
        }

        val name: String = args.subList(1, args.lastIndex - 1).joinToString(" ")

        if (name != "/all") {
            val initialTask = tasksRepository.findByName(name)
            if (initialTask == null) {
                returnedMessage = "Не о чем уведомлять"
                return returnedMessage
            }
        } else {
            val tasks = tasksRepository.findAll()
            if (Iterables.isEmpty(tasks)) {
                returnedMessage = "Ой, здесь пусто UwU"
                return returnedMessage
            }
        }

        returnedMessage = "Уведомления включены"
        val action = Runnable {
            val executableMessage = StringBuilder("Какой прекрасный день! Самое время заняться тем, что давно запланировано:\n\n")
            if (name == "/all") {
                executableMessage.append(getAll())
            } else {
                val task = tasksRepository.findByName(name)
                if (task != null) {
                    val message = getStringFromTaskModel(task)
                    executableMessage.append(message)
                }
            }
            val sendMessage = SendMessage(chatId.toString(), executableMessage.toString())
            sendMessage.enableMarkdown(true)

            sender.execute(sendMessage)
        }

        if (scheduler.isShutdown)
            scheduler = Executors.newSingleThreadScheduledExecutor()

        val initialDelay = getInitialDelay(time, date)
        val timeUnit = TimeUnit.SECONDS
        val period = getPeriod(freq)

        if (freq == defaultFreq) scheduler.schedule(action, initialDelay, timeUnit)
        else scheduler.scheduleAtFixedRate(action, initialDelay, period, timeUnit)

        return returnedMessage
    }

    fun asyncTaskCheck(chatId: Long, sender: AbsSender) {
        if (!deadlineChecker.isShutdown) deadlineChecker.shutdown()

        deadlineChecker = Executors.newSingleThreadScheduledExecutor()
        val action = Runnable {
            val currentTime = ZonedDateTime.now()
            val todoTasks = tasksRepository.findAll()
            val expiredTasks = todoTasks.filter { it.status == Status.TODO && it.deadline!! < currentTime }
            if (expiredTasks.isNotEmpty()) {
                val executableMessage = StringBuilder(
                    "А сроки то горят! У некоторых ваших задач просрочен дедлайн:\n\nназвание :      срок\n"
                )
                for (task: TaskModel in expiredTasks) {
                    executableMessage.append(
                        "\t${task.name} :  ${task.deadline?.toLocalDate()} ${task.deadline?.toLocalTime()}\n"
                    )
                }
                sender.execute(SendMessage(chatId.toString(), executableMessage.toString()))
            }
        }
        val updatePeriod = 30L
        val initialDelay = 0L
        val minutes = TimeUnit.MINUTES
        deadlineChecker.scheduleAtFixedRate(action, initialDelay, updatePeriod, minutes)

    }

}