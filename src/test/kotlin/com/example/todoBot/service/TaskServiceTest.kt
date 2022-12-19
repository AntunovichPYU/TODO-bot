package com.example.todoBot.service

import com.example.todoBot.enums.Status
import com.example.todoBot.model.TaskModel
import com.example.todoBot.repository.TasksRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [ContextInitializer::class])
internal class TaskServiceTest(
    @Autowired private val taskService: TaskService,
    @Autowired private val tasksRepository: TasksRepository
) {

    @BeforeEach
    fun beforeEach() {
        tasksRepository.deleteAll()
    }

    @Test
    fun create() {
        val inputString = "/add wrong message "
        val args = inputString.split(" ")
        val reply = taskService.create(args)

        assertEquals("Я не понимаю такой формат даты :P", reply)
    }

    @Test
    fun update() {
        val inputString = "/edit simple task DONE"
        val task = TaskModel()
        task.id = 1
        task.createdAt = ZonedDateTime.now()
        task.name = "simple task"
        task.deadline = ZonedDateTime.now().plusDays(1)
        task.status = Status.TODO
        tasksRepository.save(task)
        val reply = taskService.update(inputString.split(" "))

        assertEquals("Статус задачи успешно изменен!", reply)
        assertTrue(tasksRepository.findByName("simple task")?.status == Status.DONE)
    }

    @Test
    fun delete() {
        val objectToDelete = "simple task"
        val inputString = "/delete $objectToDelete"
        val task = TaskModel()
        task.id = 1
        task.createdAt = ZonedDateTime.now()
        task.name = objectToDelete
        task.deadline = ZonedDateTime.now().plusDays(1)
        task.status = Status.TODO
        tasksRepository.save(task)

        assertTrue(tasksRepository.findByName(objectToDelete) != null)
        val reply = taskService.delete(inputString.split(" "))
        assertEquals("Задача успешна удалена!", reply)
        assertTrue(tasksRepository.findByName(objectToDelete) == null)
    }

    @Test
    fun getAll() {
        val firstReply = taskService.getAll()
        assertEquals("Ой, здесь пусто UwU", firstReply)

        val task = TaskModel()
        val createDate = ZonedDateTime.now(ZoneId.of("Europe/Moscow"))
        val deadlineDate = ZonedDateTime.now().plusDays(1)
        task.id = 1
        task.createdAt = createDate
        task.name = "simple task"
        task.deadline = deadlineDate
        task.status = Status.TODO
        tasksRepository.save(task)

        val secondReply = taskService.getAll()
        val expectedReply = "*Название:* ${task.name},  *срок:* ${deadlineDate.toLocalDate()} ${deadlineDate.toLocalTime()},  *статус:* ${task.status}\n"
        assertEquals(expectedReply, secondReply)
    }
}