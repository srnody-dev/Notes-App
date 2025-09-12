package com.example.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface NotesDao {

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteSync(noteId: Int): NoteWithContentDbModel?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDbModel>>

    @Transaction
    @Query("SELECT DISTINCT notes.*  FROM notes JOIN content ON notes.id==content.noteId WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' AND contentType == 'TEXT' ORDER BY updatedAt DESC  ")
    fun searchNotes(query: String): Flow<List<NoteWithContentDbModel>>

    @Query("DELETE FROM notes WHERE id == :noteId   ")
    suspend fun deleteNote(noteId: Int)

    @Transaction
    @Query("DELETE FROM content WHERE noteId == :noteId   ")
    suspend fun deleteNoteContent(noteId: Int)

    @Transaction
    @Query("SELECT * FROM notes WHERE id== :noteId")
    fun getNote(noteId: Int): Flow<NoteWithContentDbModel?>


    @Query("UPDATE notes SET isPin = NOT isPin WHERE id== :noteId ")
    suspend fun switchPinnedStatus(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(noteDbModel: NoteDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDbModel>)

    @Transaction
    suspend fun addNoteWithContent(
        content: List<ContentItemDbModel>,
        noteDbModel: NoteDbModel
    ) {
        val noteId =
            addNote(noteDbModel).toInt() //этот обьект сохраняем в базу,после чего получаем id который сгенерировала база данных
        val currentNoteId = content.map {
            it.copy(noteId=noteId) // берем готовые DbModel и обновляет в них noteId
        }
        addNoteContent(currentNoteId) //мы сохраняем ее в базу
    }




    @Transaction
    suspend fun updateNote(
        content: List<ContentItemDbModel>,
        noteDbModel: NoteDbModel,
    ) {
        addNote(noteDbModel)
        deleteNoteContent(noteDbModel.id)
        addNoteContent(content)
    }
}