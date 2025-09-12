package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note


//Mapper необходим для преобразования данных data слоя в domain слой

fun Note.toDbModel(): NoteDbModel { // преобразовываем noteDbModel в Note   (domain слой в data слой)


    return NoteDbModel(id, title, updatedAt, isPin)
}

fun List<ContentItem>.toContentItemDbModels(noteId: Int): List<ContentItemDbModel> { //domain-data
    return mapIndexed { index, contentItem ->
        when (contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel(
                    contentType = ContentType.IMAGE,
                    noteId = noteId,
                    order = index,
                    content = contentItem.url
                )
            }

            is ContentItem.Text -> {
                ContentItemDbModel(
                    contentType = ContentType.TEXT,
                    noteId = noteId,
                    order = index,
                    content = contentItem.content
                )

            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {  //data-domain
    return map { contentItem ->
        when(contentItem.contentType){
            ContentType.TEXT -> {
                ContentItem.Text(content = contentItem.content)
            }
            ContentType.IMAGE -> {
                ContentItem.Image(url = contentItem.content)
            }
        }
    }
}


fun NoteWithContentDbModel.toEntity(): Note { // преобразовываем NoteWithContentDbModel. в Note      Entity-модели domain слоя-сущность

    return Note(
        id = noteDbModel.id,
        title = noteDbModel.title,
        content = content.toContentItems(),
        updatedAt = noteDbModel.updatedAt,
        isPin = noteDbModel.isPin
    )
}

fun List<NoteWithContentDbModel>.toEntities(): List<Note> {
    return map { it.toEntity() }
}