package com.pepsigo.admin.fakeTesting

import com.pepsigo.admin.model.AddUserRequest
import com.pepsigo.admin.model.EditUserRequest
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserSuccessResponse
import com.pepsigo.admin.repository.UserRepository

class FakeUserRepository : UserRepository {

    // keep a mutable list in memory to simulate DB
    private val users = mutableListOf(
        User(
            id = 1,
            name = "Hitesh Kumar",
            businessName = "Manjunatha Agencies",
            address1 = "Kuppam Gudipalli Road",
            address2 = "Kuppam",
            state = "Andhra Pradesh",
            pincode = "517425",
            latitude = 12.9716,
            longitude = 77.5946,
            role = "customer",
            enabled = true,
            mobile = "9876543210",
            locationId = 101,
            email = "hitesh@example.com"
        ),
        User(
            id = 2,
            name = "Anita Singh",
            businessName = "Singh Traders",
            address1 = "MG Road",
            address2 = "Bangalore",
            state = "Karnataka",
            pincode = "560001",
            latitude = 12.2958,
            longitude = 76.6394,
            role = "customer",
            enabled = true,
            mobile = "9123456780",
            locationId = 102,
            email = "anita@example.com"
        ),
        User(
            id = 3,
            name = "Ravi Narayan",
            businessName = "Fresh Mart Wholesale",
            address1 = "KR Market",
            address2 = "Bangalore",
            state = "Karnataka",
            pincode = "560002",
            latitude = 12.9629,
            longitude = 77.5806,
            role = "vendor",
            enabled = true,
            mobile = "9012345678",
            locationId = 201,
            email = "ravi@freshmart.com"
        ),
        User(
            id = 4,
            name = "Meena Sharma",
            businessName = "Sharma Distributors",
            address1 = "Main Road",
            address2 = "Chennai",
            state = "Tamil Nadu",
            pincode = "600001",
            latitude = 13.0827,
            longitude = 80.2707,
            role = "vendor",
            enabled = false, // keep one inactive for testing toggle
            mobile = "9098765432",
            locationId = 202,
            email = "meena@sharmadistributors.com"
        ),
        User(
            id = 5,
            name = "Arun Kumar",
            businessName = "N/A",
            address1 = "--",
            address2 = "--",
            state = "--",
            pincode = "--",
            latitude = 0.0,
            longitude = 0.0,
            role = "Delivery Executive",
            enabled = true,
            mobile = "9876543210",
            locationId = -1,
            email = "arun.kumar@deliveryapp.com"
        ),
        User(
            id = 6,
            name = "Meena Sharma",
            businessName = "N/A",
            address1 = "--",
            address2 = "--",
            state = "--",
            pincode = "--",
            latitude = 0.0,
            longitude = 0.0,
            role = "Delivery Executive",
            enabled = false, // disabled user
            mobile = "9123456789",
            locationId = -1,
            email = "meena.sharma@deliveryapp.com"
        ),
        User(
            id = 7,
            name = "Ravi Narayan",
            businessName = "N/A",
            address1 = "--",
            address2 = "--",
            state = "--",
            pincode = "--",
            latitude = 0.0,
            longitude = 0.0,
            role = "Delivery Executive",
            enabled = true,
            mobile = "9012345678",
            locationId = -1,
            email = "ravi.narayan@deliveryapp.com"
        ),

    )

    override suspend fun getUsers(role: String): Result<List<User>> {
        return Result.success(users.filter { it.role == role })
    }

    override suspend fun getUserById(id: Int): Result<User> {
        val user = users.find { it.id == id }
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("User with id $id not found"))
        }
    }

    override suspend fun addCustomer(form: AddUserRequest): Result<User> {
        val newId = (users.maxOfOrNull { it.id } ?: 0) + 1
        val newUser = User(
            id = newId,
            name = form.name,
            businessName = form.name + " Business", // fake example
            address1 = form.address1,
            address2 = form.address2,
            state = form.state,
            pincode = form.pincode,
            latitude = form.latitude ?: 0.0,
            longitude = form.longitude ?: 0.0,
            role = "customer",
            enabled = true,
            mobile = form.mobile,
            locationId = form.locationId ?: -1,
            email = form.email?:""
        )
        users.add(newUser)
        return Result.success(newUser)
    }

    override suspend fun addVendor(form: AddUserRequest): Result<UserSuccessResponse<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(form: EditUserRequest): Result<UserSuccessResponse<User>> {
        val index = users.indexOfFirst { it.id == form.id }
        return if (index != -1) {
            val updatedUser = users[index].copy(
                name = form.userDetail.name,
                address1 = form.userDetail.address1,
                address2 = form.userDetail.address2,
                state = form.userDetail.state,
                pincode = form.userDetail.pincode,
                latitude = form.userDetail.latitude ?: 0.0,
                longitude = form.userDetail.longitude ?: 0.0,
                mobile = form.userDetail.mobile,
                locationId = form.userDetail.locationId ?: -1,
                email = form.userDetail.email?:"",
                businessName = form.userDetail.businessName?:""
            )
            users[index] = updatedUser
            Result.success(UserSuccessResponse(
                message = "User updated successfully",
                data = updatedUser
                )
            )
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun toggleUserStatus(id: Int): Result<UserSuccessResponse<User>>{
        val index = users.indexOfFirst { it.id == id }
        return if (index != -1) {
            val updated = users[index].copy(enabled = !users[index].enabled)
            users[index] = updated
            Result.success(UserSuccessResponse(
                message = "User updated successfully",
                data = updated
            ))
        } else {
            Result.failure(Exception("User not found"))
        }
    }
}