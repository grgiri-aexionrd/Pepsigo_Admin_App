package com.pepsigo.admin.fakeTesting

import com.pepsigo.admin.model.ExecutiveStatus
import com.pepsigo.admin.model.RouteStatus
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserSuccessResponse
import com.pepsigo.admin.repository.DeliveryExecutiveStatusRepo
import com.pepsigo.admin.screens.deliveryExecutive.NewDelForm
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.delay

class FakeDeliveryExecutiveStatusRepo : DeliveryExecutiveStatusRepo {
    override suspend fun fetchDeliveryExecutiveStatuses(): Result<List<ExecutiveStatus>> {
//        val fakeStatuses = listOf(
//            ExecutiveStatus(
//                id = 5,
//                status = "Assigned",
//                route = RouteStatus(
//                    id = 101,
//                    name = "Koramangala Route",
//                    assignmentStatus = "In Progress"
//                )
//            ),
//            ExecutiveStatus(
//                id = 7,
//                status = "Assigned",
//                route = RouteStatus(
//                    id = 102,
//                    name = "Indiranagar Route",
//                    assignmentStatus = "Accepted"
//                )
//            )
//            // Notice: id=2 and id=5 missing → will map to "Unknown"
//        )
        return Result.success(emptyList())
    }

    override suspend fun addDeliveryExec(form: NewDelForm): Result<UserSuccessResponse<User>> {
        return try {
            // simulate network latency
            delay(400)

            // create a fake user domain object
            val createdUser = User(
                id = 999, // fake id
                name = form.name,
                email = form.email,
                mobile = form.mobile,
                enabled = true,
                businessName = "ftr",
                address1 = "ftr",
                address2 = "ftr",
                state = "ftr",
                pincode = "ftr",
                latitude = 1.11,
                longitude = 1.11,
                role = "ftr",
                locationId = 5 // default for fake
            )

            val response = UserSuccessResponse(
                message = "Delivery executive added successfully",
                data = createdUser
            )

            Result.success(response)
        } catch (t: Throwable) {
            // Keep repository contract: failures carry AppError
            Result.failure(AppError.Unknown(t.message ?: "Fake repo error", t))
        }
    }

}
