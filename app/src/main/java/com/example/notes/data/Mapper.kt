package com.example.notes.data

import com.example.notes.domain.Note




//Mapper необходим для преобразования данных data слоя в domain слой

fun Note.toDbModel():NoteDbModel{ // преобразовываем noteDbModel в Note   (domain слой в data слой)


    return NoteDbModel(id, title, content, updatedAt, isPin)
}

fun NoteDbModel.toEntity():Note{ // преобразовываем noteDbModel в Note      Entity-модели domain слоя-сущность
    return Note(id, title, content, updatedAt, isPin)
}
fun List<NoteDbModel>.toEntities():List<Note>{
    return map { it.toEntity() }
}