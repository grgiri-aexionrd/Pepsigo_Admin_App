package com.pepsigo.admin.screens.commonComponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.pepsigo.admin.constants.DateSelectionMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun ModalDatePicker(
    modifier: Modifier = Modifier,
    label: String? =null,
    error: Boolean = false,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    mode: DateSelectionMode = DateSelectionMode.PAST_OR_TODAY
) {
    val today = remember { System.currentTimeMillis() }
    val datePickerState = rememberDatePickerState(
        selectableDates  = object : SelectableDates {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                val todayDate = Instant.ofEpochMilli(today)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return when (mode) {
                    DateSelectionMode.PAST_OR_TODAY -> selectedDate <= todayDate
                    DateSelectionMode.TODAY_OR_FUTURE -> selectedDate >= todayDate
                }
            }
        }
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = convertMillisToDate(millis)
                        onDateSelected(date)      // ✅ CALLING HERE
                        onDismiss()
                    }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }

}
//fun convertMillisToDate(millis: Long): String {
//    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    return sdf.format(Date(millis))
//}


