package com.example.thecodecup.domain.usecase.profile

import com.example.thecodecup.domain.models.UserUpdateModel
import com.example.thecodecup.domain.repository.UserRepository

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        email: String,
        fullName: String,
        phone: String,
        avatarUrl: String?,
        address: String,
        oldPassword: String?,
        newPassword: String?,
        confirmNewPassword: String?
    ): Result<Unit> {
        val isChangingPassword = oldPassword != null || newPassword != null || confirmNewPassword != null
        if (isChangingPassword) {
            if (oldPassword.isNullOrBlank() || newPassword.isNullOrBlank() || confirmNewPassword.isNullOrBlank()) {
                return Result.failure(Exception("All password fields are required"))
            }
            if (newPassword != confirmNewPassword) {
                return Result.failure(Exception("New passwords do not match"))
            }
        }

        val userUpdateModel = UserUpdateModel(
            email = email,
            fullName = fullName,
            phoneNumber = phone,
            avatarUrl = avatarUrl,
            address = address,
            oldPassword = oldPassword,
            newPassword = newPassword
        )

        val result = userRepository.updateUser(userUpdateModel)
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
