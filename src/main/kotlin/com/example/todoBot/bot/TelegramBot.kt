package com.example.todoBot.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class TelegramBot: TelegramLongPollingBot() {
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
                when (message.text) {
                    "/start" -> execute(getWelcomeMessage(chatId))
                    else -> execute(getIllegalMessage(chatId))
                }
            } else {
                execute(getIllegalMessage(chatId))
            }
        }
    }

    private fun getIllegalMessage(chatId: Long): SendMessage {
        val illegalMessage = SendMessage(chatId.toString(), "Я не понимаю, о чем вы говорите")
        illegalMessage.enableMarkdown(true)
        return illegalMessage
    }

    private fun getWelcomeMessage(chatId: Long): SendMessage {
        val welcomeMessage = SendMessage(chatId.toString(), "Добро пожаловать! Введите команду /help, " +
                "чтобы узнать возможности бота")
        welcomeMessage.enableMarkdown(true)
        return welcomeMessage
    }

}