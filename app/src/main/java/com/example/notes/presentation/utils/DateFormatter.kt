package com.example.notes.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object DateFormatter {
    private val millisInSec = TimeUnit.SECONDS.toMillis(60)

    private val millisInMinut = TimeUnit.MINUTES.toMillis(60)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)


    fun formanCurrentDay(): String {
        return formatter.format(System.currentTimeMillis())
    }


    fun formatDateToString(timeStamp: Long): String { // преобразование миллисекунд в понятный вид
        val now = System.currentTimeMillis() // текущее количество миллисекунд
        val diff = now - timeStamp // разница между временами

        return when {
            diff < millisInSec -> "Just now" // если прошло меньше часа

            diff < millisInMinut -> {
                val minutes= TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes m ago"
            } // если прошло меньше часа

            diff < millisInDay -> { // если прошло меньше дня то выводим количество часов назад
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours h ago"
            }

            else -> { // в других случаях показываем дату
                formatter.format(timeStamp)
            }
        }
    }
}