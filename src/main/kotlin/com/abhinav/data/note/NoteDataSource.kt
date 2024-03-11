package com.abhinav.data.note

import com.abhinav.data.response.NoteResponse
import org.bson.types.ObjectId

interface NoteDataSource {
    suspend fun insertNote(id: ObjectId, note: Note) : Boolean
    suspend fun updateNote(id: ObjectId, note: Note) : Boolean
    suspend fun deleteNote(id: ObjectId, noteId: ObjectId) : Boolean
    suspend fun getNotesList(id: ObjectId) : List<NoteResponse>?
    suspend fun getNoteById(id: ObjectId, noteId: ObjectId) : NoteResponse?
}