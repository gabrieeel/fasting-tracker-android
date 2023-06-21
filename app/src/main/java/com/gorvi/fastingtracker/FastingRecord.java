package com.gorvi.fastingtracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDateTime;

@Entity(tableName = "fasting_records")
public class FastingRecord {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public FastingRecord(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
