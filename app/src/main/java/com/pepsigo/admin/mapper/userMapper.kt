package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserDto

fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name.ifBlank { "Unknown" },
        businessName = businessName ?: "—",
        address1 = address1 ?: "—",
        address2 = address2 ?: "—",
        state = state ?: "—",
        pincode = pincode ?: "—",
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        role = role,
        enabled = enabled,
        mobile = mobile.ifBlank { "—" },
        locationId = locationId ?: -1,
        email = email.ifBlank { "—" }
    )
}

