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

    override suspend fun insertUser(user: UserModel, password: String) {
        val userEntity = user.toEntity(password)
        userDao.insert(userEntity)
    }
}