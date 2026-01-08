package com.pepsigo.admin.screens.reports

import android.graphics.fonts.FontStyle
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.screens.commonComponents.ModalDatePicker


/**
 * Inline date range row that opens a modal DateRangePicker when tapping From or To.
 * This component is independent of the project's DockedDatePicker and does not modify it.
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun InlineDateRangePicker(
    modifier: Modifier = Modifier,
    label: String = "Date Range",
    fromDate: String ,
    toDate: String ,
    error: Boolean = false,
    onFromDateSelected: (String) -> Unit,
    onToDateSelected: (String) -> Unit,
    onRangeSelected: (from: String, to: String) -> Unit = { _, _ -> }
) {
    // Local UI state for showing the range picker
    var showPicker by remember { mutableStateOf(false) }
    var activeMode: DateField? by remember { mutableStateOf(null) }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Static label (no dropdown)
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(12.dp))

            // Vertical divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(color = Color(0xFFE1E6EE))
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Calendar icon
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Calendar",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Dates (open modal range picker on click)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = fromDate,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier
                        .widthIn(80.dp)
                        .border(1.dp, if(error) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onPrimary)
                        .clickable {
                        activeMode = DateField.FROM
                        showPicker = true }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = "Calendar",
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = toDate,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier
                        .widthIn(80.dp)
                        .border(1.dp, if(error) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onPrimary)
                        .clickable {
                        activeMode = DateField.TO
                        showPicker = true }
                )
            }
        }
    }

    // Range DatePicker popup
    if (showPicker && activeMode != null) {

        ModalDatePicker(
            onDateSelected = { date ->
                when (activeMode) {
                    DateField.FROM -> { onFromDateSelected(date) }
                    DateField.TO -> { onToDateSelected(date) }
                    else -> {}
                }
            },
            onDismiss = {
                activeMode = null
                showPicker = false
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun InlineDateRangePickerPreview() {
//    InlineDateRangePicker()
}
