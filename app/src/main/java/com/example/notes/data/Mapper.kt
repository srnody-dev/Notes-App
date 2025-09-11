package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import kotlinx.serialization.json.Json


//Mapper необходим для преобразования данных data слоя в domain слой

fun Note.toDbModel():NoteDbModel{ // преобразовываем noteDbModel в Note   (domain слой в data слой)


    val contentAsString = Json.encodeToString(content.toContentItemDbModels())
    return NoteDbModel(id, title, contentAsString, updatedAt, isPin)
}

fun List<ContentItem>.toContentItemDbModels(): List<ContentItemDbModel>{ //domain-data
    return map { contentItem ->
        when(contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel.Image(url = contentItem.url)
            }
            is ContentItem.Text -> {
                ContentItemDbModel.Text(content = contentItem.content)
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem>{  //data-domain
    return map { contentItem ->
        when(contentItem) {
            is ContentItemDbModel.Image -> {
                ContentItem.Image(url = contentItem.url)
            }
            is ContentItemDbModel.Text -> {
                ContentItem.Text(content = contentItem.content)
            }
        }
    }
}


fun NoteDbModel.toEntity():Note{ // преобразовываем noteDbModel в Note      Entity-модели domain слоя-сущность

    val contentItemDbModels = Json.decodeFromString<List<ContentItemDbModel>>(content)

    return Note(id, title, contentItemDbModels.toContentItems(), updatedAt, isPin)
}
fun List<NoteDbModel>.toEntities():List<Note>{
    return map { it.toEntity() }
}