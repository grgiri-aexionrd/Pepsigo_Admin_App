package com.pepsigo.admin.model

data class UserForm(
    val id: Int? = null,
    val name: String = "",
    val businessName: String? = null,
    val mobile: String = "",
    val email: String = "",
    val address1: String = "",
    val address2: String = "",
    val state: String = "",
    val pincode: String = "",
    var locationId: Int? = null,
    val coordinates: String = "",

    )
