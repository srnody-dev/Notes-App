package com.example.notes.di

import android.content.Context
import androidx.room.Room
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
            // return NotesDatabase.getInstance(context)
            return Room.databaseBuilder(
                context = context,
                klass = NotesDatabase::class.java,
                name = "notes.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }


        @Singleton
        @Provides
        fun providesNotesDao(database: NotesDatabase): NotesDao {
            return database.notesDao()
        }

    }
}