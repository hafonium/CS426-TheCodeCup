package com.example.thecodecup.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.thecodecup.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getById(userId: Int): UserEntity?

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM User WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): UserEntity?

    @Insert
    suspend fun register(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("UPDATE User SET email = :email, fullName = :fullName, phoneNumber = :phoneNumber, avatarUrl = :avatarUrl WHERE id = :userId")
    suspend fun updateExceptPassword(userId: Int, email: String, fullName: String, phoneNumber: String, avatarUrl: String)

    @Query("UPDATE User SET password = :newPassword WHERE id = :userId AND password = :currentPassword")
    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email)")
    suspend fun checkIfEmailExists(email: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE phoneNumber = :phone)")
    suspend fun checkIfPhoneExists(phone: String): Boolean
}