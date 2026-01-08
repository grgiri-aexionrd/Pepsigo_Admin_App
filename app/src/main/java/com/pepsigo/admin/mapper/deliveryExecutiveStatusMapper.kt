package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.ExecutiveStatus
import com.pepsigo.admin.model.ExecutiveStatusDto
import com.pepsigo.admin.model.RouteStatus
import com.pepsigo.admin.model.RouteStatusDto

fun  ExecutiveStatusDto.toDomain(): ExecutiveStatus {
    return ExecutiveStatus(
        id = id,
        delExecName = name,
        status = status,
        route = route?.toDomain()
    )

}

fun RouteStatusDto.toDomain(): RouteStatus {
    return RouteStatus(
        id = id,
        name = name,
        assignmentStatus = status
    )
}