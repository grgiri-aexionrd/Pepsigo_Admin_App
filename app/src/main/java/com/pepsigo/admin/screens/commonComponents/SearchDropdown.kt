package com.pepsigo.admin.screens.commonComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>SearchDropDown(
    filteredDropDown: List<T>,
    error: Boolean = false,
    label: String ,
    searchQuery: String,
    selected : T? = null,
    onSelected: (T?) -> Unit,
    onSearchChange: (String) -> Unit,
    labelExtractor: (T) -> String,
){
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
//        modifier = Modifier.fillMaxWidth()
    ){
        OutlinedTextField(
            //value = displayText,
//            value = selected?.let { labelExtractor(it) } ?: "",
            value = searchQuery,
            onValueChange = {
                expanded = true
                onSearchChange(it)
            },
            label = { if (!error) Text(label) else Text(stringResource(R.string.dropdown_error)) },
            // use readonly = false to enable keypad so that searchable dropdown can be opened, to be configured with required screens.
            readOnly = false,
            isError = error,
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = ""
                )
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryEditable,
//                    enabled = true
                )      // ✅ Required
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ){
            Column(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            )  {
                filteredDropDown.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(labelExtractor(item)) },
                        onClick = {
                            onSelected(item)
                            onSearchChange(labelExtractor(item))
                            expanded = false
                        }
                    )
                }
            }

        }
    }

}