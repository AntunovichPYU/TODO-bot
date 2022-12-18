package com.example.todoBot.repository

import com.example.todoBot.model.TaskModel
import org.springframework.data.repository.CrudRepository

interface TasksRepository : CrudRepository<TaskModel, Long> {
    fun findByName(name: String): TaskModel?
}