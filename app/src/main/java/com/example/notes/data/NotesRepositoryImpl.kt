package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// реализация интерфейса репозитория
class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager
    ) : NotesRepository {




    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val processesContent=content.processForStorage() //обрабатываем весь контект,чтобы изобр были сохранены в Internal storage
        val noteDbModel = NoteDbModel(0,title,updatedAt, isPinned) //создаем обьект базы данных
        val contentDomainToData=processesContent.toContentItemDbModels(noteDbModel.id) // преобразовываем Domain в Data
        notesDao.addNoteWithContent(contentDomainToData,noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        val note=notesDao.getNoteSync(noteId)?.toEntity()
        notesDao.deleteNote(noteId)

        note?.content?.filterIsInstance<ContentItem.Image>()?.map { it.url }?.forEach {
            imageFileManager.deleteImage(it)
        }
    }

    override suspend fun editNote(note: Note) {
        val oldNote=notesDao.getNoteSync(note.id)?.toEntity()
        val oldUrls=oldNote?.content?.filterIsInstance<ContentItem.Image>()?.map { it.url } ?: emptyList()
        val newUrls=note.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val removeUrls=oldUrls - newUrls
        removeUrls.forEach {
            imageFileManager.deleteImage(it)
        }
        val processedContent=note.content.processForStorage()
        val processedNote=note.copy(content = processedContent)
        notesDao.updateNote(processedContent.toContentItemDbModels(note.id),processedNote.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override fun getNote(noteId: Int): Flow<Note?> {
        return notesDao.getNote(noteId).map { it?.toEntity() }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }
    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem>{
        return map { contentItem ->
            when(contentItem){
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)){
                        contentItem
                    }
                    else {
                       val internalPath= imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(internalPath)
                    }

                }
                is ContentItem.Text -> {
                    contentItem
                }
            }

        }
    }

}