package com.example.thecodecup.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thecodecup.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM User WHERE id = :userId")
    fun getById(userId: Int): UserEntity?

    @Insert
    fun insert(user: UserEntity)
}