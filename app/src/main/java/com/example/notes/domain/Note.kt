package com.example.notes.domain


data class Note( //сущность с которой будут работать data и presentation слои //уже с ними будет работать база данный Db


    val id:Int,
    val title:String,
    val content: List<ContentItem>,
    val updatedAt:Long,
    val isPin:Boolean

)
