package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.LocationDto
import com.pepsigo.admin.screens.location.Location

fun LocationDto.toDomain(): Location = Location(
    id = id,
    name = name,
    isEnabled = enabled
)