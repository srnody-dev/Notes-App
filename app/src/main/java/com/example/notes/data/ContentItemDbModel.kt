package com.example.notes.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "content",
    primaryKeys = ["noteId", "order"],
    foreignKeys = [
        ForeignKey(
            entity = NoteDbModel::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ContentItemDbModel(
    val noteId: Int,
    val order: Int,
    val contentType: ContentType,
    val content: String


)

enum class ContentType {
    TEXT, IMAGE
}