package com.example.todoBot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BotKotlinApplication

fun main(args: Array<String>) {
	runApplication<BotKotlinApplication>(*args)
}
