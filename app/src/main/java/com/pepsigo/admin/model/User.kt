package com.pepsigo.admin.model

data class User(
    val id: Int,
    val name: String,
    val businessName: String,
    val address1: String,
    val address2: String,
    val state: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val role: String,
    val enabled: Boolean,
    val mobile: String,
    val locationId: Int,
    val email: String,
)
