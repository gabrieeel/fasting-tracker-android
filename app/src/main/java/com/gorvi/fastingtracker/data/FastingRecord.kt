package com.gorvi.fastingtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDateTime

@Entity(tableName = "fasting_records")
class FastingRecord(var startTime: LocalDateTime, var endTime: LocalDateTime?) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun duration(): Duration =
        endTime?.let { Duration.between(startTime, it) } ?: Duration.ZERO


    override fun toString(): String {
        var s = "Start Time: ${this.startTime}" +
                "\nEnd Time: ${this.endTime}"
        if (this.duration().isZero) {
            return s
        }
        return s + "\nDuration: ${this.duration().toString().substring(2)}"
    }
}