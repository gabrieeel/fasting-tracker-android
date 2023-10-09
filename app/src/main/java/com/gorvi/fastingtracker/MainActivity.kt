package com.gorvi.fastingtracker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class MainActivity : AppCompatActivity(), DateTimeDialogCallback {
    private lateinit var buttonStartFasting: Button
    private lateinit var buttonEndFasting: Button
    private lateinit var listView: ListView
    private val list: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var fastingRecordRepository: FastingRecordRepository

    private lateinit var defaultDate: LocalDate
    private lateinit var defaultTime: LocalTime
    private lateinit var dateFormatter: DateTimeFormatter
    private lateinit var timeFormatter: DateTimeFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create an instance of the repository
        val fastingRecordDao = AppDatabase.getInstance(applicationContext).fastingRecordDao()
        fastingRecordRepository = FastingRecordRepository(fastingRecordDao)

        // buttons
        buttonStartFasting = findViewById(R.id.buttonStartFasting)
        buttonEndFasting = findViewById(R.id.buttonEndFasting)

        // for date and time picker
        dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

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

    private fun startFasting(startTime: LocalDateTime) {
        GlobalScope.launch(Dispatchers.Main) {
            var fastingRecord = FastingRecord(startTime, null)
            fastingRecordRepository.insertFastingRecord(fastingRecord)

            refreshFastingRecords()
            updateButtonVisibility(true)
        }
    }

    private fun endFasting(endTime: LocalDateTime) {
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

    fun showDateTimeDialog(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.datetime_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val dateField = dialogView.findViewById<EditText>(R.id.dateField)
        val timeField = dialogView.findViewById<EditText>(R.id.timeField)

        // Set current date and time as defaults for the dialog
        defaultDate = LocalDate.now()
        defaultTime = LocalTime.now()
        dateField.setText(defaultDate.format(dateFormatter))
        timeField.setText(defaultTime.format(timeFormatter))

        // Date field click listener
        dateField.setOnClickListener {
            showDatePicker(dateField)
        }

        // Time field click listener
        timeField.setOnClickListener {
            showTimePicker(timeField)
        }

        dialogBuilder.setTitle(R.string.select_date_and_time)

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            val selectedDate = dateField.text.toString()
            val selectedTime = timeField.text.toString()
            (this as? DateTimeDialogCallback)?.onOkButtonClicked(selectedDate, selectedTime, view)
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun showDatePicker(dateField: EditText) {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
//                calendar.set(year, month, dayOfMonth)
                dateField.setText(dateFormatter.format(pickedDate))
            },
            defaultDate.year,
            defaultDate.monthValue - 1,
            defaultDate.dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(timeField: EditText) {
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val pickedTime = LocalTime.of(hourOfDay, minute)
                timeField.setText(timeFormatter.format(pickedTime))
            },
            defaultTime.hour,
            defaultTime.minute,
            true
        )
        timePickerDialog.show()
    }

    override fun onOkButtonClicked(selectedDate: String, selectedTime: String, view: View) {
        var selectedDateTime = LocalDateTime.of(LocalDate.parse(selectedDate, dateFormatter), LocalTime.parse(selectedTime, timeFormatter))

        when (view) {
            buttonStartFasting -> {
                // Handle "start fasting" button click
                Log.i("MA", "start")
                startFasting(selectedDateTime)
            }
            buttonEndFasting -> {
                // Handle "end fasting" button click
                endFasting(selectedDateTime)
            }
        }
    }
}