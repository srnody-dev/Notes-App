package com.example.notes.di

import android.content.Context
import androidx.room.Database
import com.example.notes.data.NotesDao
import com.example.notes.data.NotesDatabase
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsRepository(repositoryImpl: NotesRepositoryImpl): NotesRepository

    companion object {
        @Singleton
        @Provides
        fun providesDatabase(@ApplicationContext context: Context): NotesDatabase {
            return NotesDatabase.getInstance(context)
        }

        /*
        @Singleton
        @Provides
        fun providesNotesDao(@ApplicationContext context: Context): NotesDao {
            return NotesDatabase.getInstance(context).notesDao()
        } так нельзя потому что Проблемы:

❌ Каждый раз создает новый экземпляр базы данных через getInstance(context)
❌ Нарушает паттерн Singleton - база должна создаваться только один раз
❌ Может привести к утечкам памяти и проблемам с многопоточностью
❌ Неэффективно - каждый вызов создает новое подключение к БД

        */


        @Singleton
        @Provides
        fun providesNotesDao(database: NotesDatabase): NotesDao {
            return database.notesDao()
        }

    }
}