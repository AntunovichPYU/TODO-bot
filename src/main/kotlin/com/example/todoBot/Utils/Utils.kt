package com.example.todoBot.Utils

class Utils {

    companion object {
        val dateFormat = Regex("""^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12]\d|3[01])$""")
        val timeFormat = Regex("""^([0-1]?\d|2[0-3]):[0-5]\d$""")
    }
}