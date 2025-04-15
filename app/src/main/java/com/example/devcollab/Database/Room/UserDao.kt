package com.example.devcollab.Database.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// UserDao.kt
@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_profile WHERE uid = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("DELETE FROM user_profile")
    suspend fun deleteUser()

    @Query("UPDATE user_profile SET username = :username, profession = :profession, profileImageUrl = :profileImageUrl WHERE uid = :userId")
    suspend fun updateUser(userId: Int, username: String, profession: String, profileImageUrl: String)

}
