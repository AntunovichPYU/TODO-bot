package com.example.todoBot.bot

import com.example.todoBot.service.TaskService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class TelegramBot(
    private val taskService: TaskService
): TelegramLongPollingBot() {
    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    override fun getBotToken(): String = token

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message = update.message
            val chatId = message.chatId

            if (message.hasText()) {
                val arguments = message.text.split(" ")
                when (arguments[0]) {
                    "/start" -> {
                        getWelcomeMessage(chatId)
                    }
                    "/help" -> {
                        getHelpMessage(chatId)
                    }
                    "/add" -> {
                        if (arguments.size < 4) getArgumentMessage(chatId)
                        else getAddMessage(chatId, arguments)
                    }
                    "/edit" -> {
                        if (arguments.size < 3) getArgumentMessage(chatId)
                        else getEditMessage(chatId, arguments)
                    }
                    "/delete" -> {
                        if (arguments.size < 2) getArgumentMessage(chatId)
                        else getDeleteMessage(chatId, arguments)
                    }
                    "/mytasks" -> {
                        getAllTasks(chatId)
                    }
                    "/notify" -> {
                        if (arguments.size < 4) getArgumentMessage(chatId)
                        else getNotificationMessage(chatId, arguments)
                    }
                    else -> getIllegalMessage(chatId)
                }
            } else {
                getIllegalMessage(chatId)
            }
        }
    }

    private fun getArgumentMessage(chatId: Long) {
        val argumentMessage = SendMessage(chatId.toString(), "Недостаточно информации. /help")
        argumentMessage.enableMarkdown(true)
        execute(argumentMessage)
    }

    private fun getIllegalMessage(chatId: Long) {
        val illegalMessage = SendMessage(chatId.toString(), "Я не понимаю, о чем вы говорите. /help вам в помощь")
        illegalMessage.enableMarkdown(true)
        execute(illegalMessage)
    }

    private fun getWelcomeMessage(chatId: Long) {
        val welcomeMessage = SendMessage(chatId.toString(), "Добро пожаловать! Введите команду /help, " +
                "чтобы узнать возможности бота")
        welcomeMessage.enableMarkdown(true)
        execute(welcomeMessage)
    }

    private fun getHelpMessage(chatId: Long) {
        val helpMessage = SendMessage(chatId.toString(), "Список доступных команд:\n\n" +
                "/start - приветственное сообщение\n" +
                "/add `{name}` `{deadline date}` `{deadline time}` - добавление новой задачи." +
                "Формат даты: _YYYY-MM-DD HH:mm_. Новая задача имеет статус _TODO_\n" +
                "/edit `{name}` `{new status}` - изменение статуса у уже существующей задачи. Варианты статусов: " +
                "\n  _TODO_ - предстоит сделать, \n  _DONE_ - готово, \n  _DELAYED_ - отложено на неопределенный срок\n" +
                "/delete `{name}` - удаление задачи\n" +
                "/mytasks - список текущих задач\n" +
                "/notify `{task}` `{time}` `{freq}`  - настроить частоту оповещений о выбранной задаче. " +
                "Альтернативно:\n/notify `{task}` `{date}` `{time}`." +
                "\n  _DAILY_ - ежедневно,\n  _WEEKLY_ - еженедельно,\n  _MONTHLY_ - ежемесячно,\n  _ONCE_ - один раз"
                )
        helpMessage.enableMarkdown(true)
        execute(helpMessage)
    }

    private fun getAddMessage(chatId: Long, args: List<String>) {
        val addMessage = taskService.create(args)
        execute(SendMessage(chatId.toString(), addMessage))
    }

    private fun getEditMessage(chatId: Long, args: List<String>) {
        val editMessage = taskService.update(args)
        execute(SendMessage(chatId.toString(), editMessage))
    }

    private fun getDeleteMessage(chatId: Long, args: List<String>) {
        val deleteMessage = taskService.delete(args)
        execute(SendMessage(chatId.toString(), deleteMessage))
    }

    private fun getAllTasks(chatId: Long) {
        val getMessage = taskService.getAll()
        val sendMessage = SendMessage(chatId.toString(), "Ваши задачи: \n\n$getMessage")
        sendMessage.enableMarkdown(true)
        execute(sendMessage)
    }

    private fun getNotificationMessage(chatId: Long, args: List<String>) {
        val getMessage = taskService.setNotification(chatId, this, args)
        val sendMessage = SendMessage(chatId.toString(), getMessage)
        sendMessage.enableMarkdown(true)
        execute(sendMessage)
    }
}