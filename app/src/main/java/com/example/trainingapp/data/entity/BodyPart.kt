package com.example.trainingapp.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_parts")
data class BodyPart(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String
)