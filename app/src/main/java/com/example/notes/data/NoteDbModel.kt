package com.example.notes.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes")

data class NoteDbModel( //сущность с которой будут работать data и presentation слои

    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val title:String,
    val content:String,
    val updatedAt:Long,
    val isPin:Boolean

)
