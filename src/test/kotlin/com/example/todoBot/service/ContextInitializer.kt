package com.example.todoBot.service

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class ContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.datasource.url=${postgresContainer.jdbcUrl}",
            "spring.datasource.username=${postgresContainer.username}",
            "spring.datasource.password=${postgresContainer.password}"
        ).applyTo(applicationContext)
    }

    companion object {
        private val postgresContainer = PostgreSQLContainer("postgres:14")
            .withDatabaseName("tasks_db_test")
            .withUsername("postgres")
            .withPassword("1234")

        init {
            postgresContainer.start()
        }
    }
}