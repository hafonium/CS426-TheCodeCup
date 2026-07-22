package com.example.thecodecup.domain.usecases.auth

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresExtension
import com.example.thecodecup.domain.models.UserCreateModel
import com.example.thecodecup.domain.repositories.UserRepository

// Use case for registering a new user
class RegisterUseCase(
    private val userRepository: UserRepository
) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend operator fun invoke(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        address: String
    ): Result<Unit> {
        if(fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return Result.failure(Exception("All fields are required"))
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }

        if(password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }

        val user = UserCreateModel(
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phone,
            address = address 
        )

        val result = userRepository.registerUser(user)
        return result.fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}