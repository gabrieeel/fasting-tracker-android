package com.gorvi.fastingtracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "fasting_records")
class FastingRecord(var startTime: LocalDateTime, var endTime: LocalDateTime?) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "Start Time: ${this.startTime}\n" +
                "End Time: ${this.endTime}"
    }
}