package com.example.trainingapp.data.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = BodyPart::class,
            parentColumns = ["id"],
            childColumns = ["body_part_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("body_part_id")]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "body_part_id")
    val bodyPartId: Long,

    val name: String,
    val description: String,
    val youtube_url: String
)