package com.gorvi.fastingtracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FastingRecordDao {
    @Insert
    void addFastingRecord(FastingRecord fastingRecord);

    @Update
    void updateFastingRecord(FastingRecord fastingRecord);

    @Delete
    void deleteFastingRecord(FastingRecord fastingRecord);

    @Query("SELECT * FROM fasting_records ORDER BY id DESC LIMIT :count")
    List<FastingRecord> getLastNFastingRecords(int count);

    @Query("SELECT * FROM fasting_records WHERE endTime IS NULL")
    FastingRecord getOngoingFastingRecord();
}
