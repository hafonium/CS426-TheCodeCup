package com.example.thecodecup.domain.usecase.account

import android.util.Log
import android.util.Patterns
import com.example.thecodecup.data.local.entities.UserEntity
import com.example.thecodecup.domain.models.UserModel
import com.example.thecodecup.domain.repository.UserRepository

// Use case for registering a new user
class RegisterUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        // Check for empty fields
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("All fields are required."))
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Invalid email format."))
        }

        // Check for the Enter key, spaces, or tabs
        if (password.any { it.isWhitespace() }) {
            return Result.failure(IllegalArgumentException("Password cannot contain spaces or newlines."))
        }

        // Check length
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        // Check for complexity (at least one letter and one number)
        if (!password.any { it.isDigit() } || !password.any { it.isLetter() }) {
            return Result.failure(IllegalArgumentException("Password must contain at least one letter and one number."))
        }

        // Ensure passwords match
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match."))
        }

        return try {
            // Check if the email is already registered in the Room Database
            val isEmailTaken = userRepository.checkIfEmailExists(email)
            val isPhoneTaken = userRepository.checkIfPhoneExists(phone)
            if (isEmailTaken) {
                return Result.failure(IllegalArgumentException("An account with this email already exists."))
            }
            if (isPhoneTaken) {
                return Result.failure(IllegalArgumentException("An account with this phone number already exists."))
            }

            val newUserModel = UserModel(
                id = 0, // Room will auto-generate the ID
                email = email,
                fullName = fullName,
                phoneNumber = phone,
                avatarUrl = ""
            )

            // Command the dumb repository to insert the row
            userRepository.registerUser(newUserModel, password)

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("RegisterUseCase", "Database crash during registration", e)
            Result.failure(Exception("Registration failed! Please try again later."))
        }
    }
}