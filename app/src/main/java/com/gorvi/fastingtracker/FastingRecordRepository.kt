package com.gorvi.fastingtracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FastingRecordRepository(private val fastingRecordDao: FastingRecordDao) {

    suspend fun insertFastingRecord(fastingRecord: FastingRecord) {
        withContext(Dispatchers.IO) {
            // Perform insert operation using fastingRecordDao
            fastingRecordDao.addFastingRecord(fastingRecord)
        }
    }

    suspend fun getLastNFastingRecords(n: Int): List<FastingRecord> {
        return withContext(Dispatchers.IO) {
            fastingRecordDao.getLastNFastingRecords(n)
        }
    }

    suspend fun updateFastingRecord(fastingRecord: FastingRecord) {
        return withContext(Dispatchers.IO) {
            fastingRecordDao.updateFastingRecord(fastingRecord);
        }
    }

    suspend fun getOngoingFastingRecord(): FastingRecord? {
        return withContext(Dispatchers.IO) {
            fastingRecordDao.getOngoingFastingRecord();
        }
    }
}