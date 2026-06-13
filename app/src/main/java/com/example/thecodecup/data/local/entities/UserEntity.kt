package com.example.thecodecup.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.thecodecup.domain.models.UserModel

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String
) {
    fun toDomainModel(): UserModel {
        return UserModel(
            id = id,
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber,
            avatarUrl = avatarUrl
        )
    }
}
