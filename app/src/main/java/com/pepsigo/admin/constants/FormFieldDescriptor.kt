package com.pepsigo.admin.constants

import androidx.compose.ui.text.input.KeyboardType
import com.pepsigo.admin.model.UserForm
import kotlin.reflect.KProperty1

data class FormFieldDescriptor(
    val property: KProperty1<UserForm, String>,
    val label: String,
    val keyboardType: KeyboardType = KeyboardType.Text
)