package com.example.thecodecup.data.repository

import com.example.thecodecup.data.local.dao.UserDao
import com.example.thecodecup.data.local.entities.UserEntity
import com.example.thecodecup.domain.models.UserModel
import com.example.thecodecup.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUserById(userId: Int): UserModel? {
        return userDao.getById(userId)?.toDomainModel()
    }

    override suspend fun registerUser(user: UserModel, password: String) {
        val userEntity = user.toEntity(password)
        userDao.register(userEntity)
    }

    override suspend fun loginUser(email: String, password: String): UserModel? {
        return userDao.login(email, password)?.toDomainModel()
    }

    override suspend fun updateUserExceptPassword(user: UserModel) {
        userDao.updateExceptPassword(
            userId = user.id,
            email = user.email,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            avatarUrl = user.avatarUrl
        )
    }

    override suspend fun deleteUser(userId: Int) {
        val userEntity = userDao.getById(userId)
        if (userEntity != null) {
            userDao.delete(userEntity)
        }
    }

    override suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String) {
        userDao.updatePassword(userId, currentPassword, newPassword)
    }

    override suspend fun checkIfEmailExists(email: String): Boolean {
        return userDao.checkIfEmailExists(email)
    }

    override suspend fun checkIfPhoneExists(phone: String): Boolean {
        return userDao.checkIfPhoneExists(phone)
    }
}