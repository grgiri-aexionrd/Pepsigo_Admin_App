package com.pepsigo.admin.screens.commonComponents

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.reports.DropDownList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>DropDown(
//    dropDown: List<DropDownList>,
    dropDown: List<T>,
    error: Boolean = false,
    label: String ,
    selected : T? = null,
    onSelected: (T?) -> Unit,
    labelExtractor: (T) -> String,
//    selected : DropDownList?,
//    onSelected: (DropDownList?) -> Unit,
    isCreatePurchase: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val fullDropDownList :List<T> = if (isCreatePurchase) {
        // When creating purchase → do NOT add "Select all"
        dropDown
    } else {
        listOf(
            (DropDownList(id = -1, name = stringResource(R.string.select_all)) as T )
        ) + dropDown
    }


   // val displayText = selected?.name ?: ""   // ✅ initially empty string
    Log.d("DropDown", "FullList: $fullDropDownList")
   // Log.d("DropDown", "Display text: $displayText")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            //value = displayText,
            value = selected?.let { labelExtractor(it) } ?: "",
            onValueChange = { },
            label = { if(!error) Text(label) else Text(stringResource(R.string.dropdown_error)) },
            readOnly = true,
            isError = error,
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = ""
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)      // ✅ Required
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ){
            fullDropDownList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(labelExtractor(item)) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }

    }


}