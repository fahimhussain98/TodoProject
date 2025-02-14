package com.example.projectnew.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val Title: String,
    val Description: String,
    var isCompleted: Boolean = false
)

