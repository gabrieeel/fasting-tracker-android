package com.gorvi.fastingtracker

import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var buttonStartFasting: Button
    private lateinit var buttonEndFasting: Button
    private lateinit var listView: ListView
    private val list: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var fastingRecordRepository: FastingRecordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // START REPOSITORY USE
        // Create an instance of the repository
        val fastingRecordDao = AppDatabase.getInstance(applicationContext).fastingRecordDao()
        fastingRecordRepository = FastingRecordRepository(fastingRecordDao)

        // buttons
        buttonStartFasting = findViewById(R.id.buttonStartFasting)
        buttonStartFasting.setOnClickListener(View.OnClickListener { startFasting() })
        buttonEndFasting = findViewById(R.id.buttonEndFasting)
        buttonEndFasting.setOnClickListener(View.OnClickListener { endFasting() })

        // list
        listView = findViewById<ListView>(R.id.listViewFastingRecords)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter

        refreshFastingRecords()

        lifecycleScope.launch() {
            val isFasting = (fastingRecordRepository.getOngoingFastingRecord() != null)
            updateButtonVisibility(isFasting)
        }
    }

    private fun startFasting() {
        GlobalScope.launch(Dispatchers.Main) {
            var fastingRecord = FastingRecord(LocalDateTime.now(), null)
            fastingRecordRepository.insertFastingRecord(fastingRecord)

            refreshFastingRecords()
            updateButtonVisibility(true)
        }
    }

    private fun endFasting() {
        val endTime = LocalDateTime.now()
        GlobalScope.launch(Dispatchers.Main) {
            var fastingRecord = fastingRecordRepository.getOngoingFastingRecord()
            if (fastingRecord == null) {
                throw RuntimeException("shouldnt be able to update")
            }
            fastingRecord?.endTime = endTime
            fastingRecordRepository.updateFastingRecord(fastingRecord);

            refreshFastingRecords()
            updateButtonVisibility(false)
        }
    }

    private fun updateButtonVisibility(isFasting: Boolean) {
        if (isFasting) {
            buttonStartFasting!!.visibility = View.GONE
            buttonEndFasting!!.visibility = View.VISIBLE
        } else {
            buttonStartFasting!!.visibility = View.VISIBLE
            buttonEndFasting!!.visibility = View.GONE
        }
    }

    private fun refreshFastingRecords() {
        GlobalScope.launch(Dispatchers.Main) {
            list.clear()
            val fastingRecords = fastingRecordRepository.getLastNFastingRecords(10)
            for (fastingRecord in fastingRecords) {
                val recordString = "Start Time: ${fastingRecord.startTime}\n" +
                        "End Time: ${fastingRecord.endTime}"
                list.add(recordString)
            }
            adapter.notifyDataSetChanged()
        }
    }
}