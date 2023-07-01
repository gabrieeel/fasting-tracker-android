package com.gorvi.fastingtracker

import android.view.View

interface DateTimeDialogCallback {
    fun onOkButtonClicked(selectedDate: String, selectedTime: String, view: View)
}