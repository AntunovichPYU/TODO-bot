package com.example.todoBot.model

import com.example.todoBot.enums.Status
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tasks")
class TaskModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: ZonedDateTime? = null,

    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null,

    @Column(name = "deadline", nullable = false)
    var deadline: ZonedDateTime? = null,

    @Column(name = "status", nullable = false)
    var status: Status? = null
) {

}