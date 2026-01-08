package com.pepsigo.admin.screens.commonComponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.pepsigo.admin.R
import com.pepsigo.admin.constants.DateSelectionMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun DockedDatePicker(
    modifier: Modifier = Modifier,
    label: String? = null,
    error: Boolean = false,
    onDateSelected: (String) -> Unit,
    mode: DateSelectionMode = DateSelectionMode.PAST_OR_TODAY
) {
    val today = remember { System.currentTimeMillis() }
    var showDatePicker by remember { mutableStateOf(false) }
//    val datePickerState = rememberDatePickerState()
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

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { if(error) Text(text = stringResource(R.string.date_error)) else label?.let { Text(it) } },
            readOnly = true,
            isError = error,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .height(60.dp)
                .wrapContentWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .padding(top = 64.dp)
                        .width(360.dp)  // ✅ makes it compact
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                            DatePicker(
                                state = datePickerState,
                                showModeToggle = false,
                                modifier = Modifier.height(484.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val date = convertMillisToDate(millis)
                                            onDateSelected(date)      // ✅ CALLING HERE
                                        }
                                        showDatePicker = false
                                    },
                                    modifier = Modifier.wrapContentWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("OK")
                                }
                            }
                    }
                }
            }
        }
    }
}





fun convertMillisToDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}


