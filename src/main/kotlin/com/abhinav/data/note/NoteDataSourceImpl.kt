package com.abhinav.data.note

import com.abhinav.data.response.NoteResponse
import com.abhinav.data.user.User
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class NoteDataSourceImpl(db: CoroutineDatabase) : NoteDataSource {

    private val user = db.getCollection<User>("user")

    override suspend fun insertNote(id: ObjectId ,note: Note): Boolean {
        val user = user.findOneById(id)
        if (user != null) {
            val updatedNotes = user.notes.toMutableList().apply { add(note) }
            return this.user.updateOne(User::_id eq id, setValue(User::notes, updatedNotes)).wasAcknowledged()
        } else {
            return false
        }
    }

    override suspend fun updateNote(id: ObjectId, note: Note): Boolean {
        val user = user.findOneById(id)
        if (user != null) {
            val updatedNotes = user.notes.toMutableList().map {
                if (it.id == note.id) {
                    it.copy(title = note.title, content = note.content)
                } else {
                    return false
                }
            }
            return this.user.updateOne(User::_id eq id, setValue(User::notes, updatedNotes)).wasAcknowledged()
        } else {
            return false
        }
    }

    override suspend fun deleteNote(id: ObjectId, noteId: ObjectId): Boolean {
        val user = user.findOneById(id)
        if (user != null) {
            val updatedNotes = user.notes.filterNot { it.id == noteId }
            return this.user.updateOne(User::_id eq id, setValue(User::notes, updatedNotes)).wasAcknowledged()
        } else {
            return false
        }
    }

    override suspend fun getNotesList(id: ObjectId): List<NoteResponse>? {
        val user = user.findOne(User::_id eq id)
        if (user != null) {
            val notes = user.notes.toList().map { convertToNoteResponse(it) }
            return notes
        }
        return null
    }

    override suspend fun getNoteById(id: ObjectId, noteId: ObjectId): NoteResponse? {
        val user = user.findOneById(id)
        if (user != null) {
            val note = user.notes.find { it.id == noteId }
            return note?.let { convertToNoteResponse(it) }
        }
        return null
    }

    private fun convertToNoteResponse(note: Note): NoteResponse {
        return NoteResponse(
            date = note.date,
            title = note.title,
            content = note.content
        )
    }
}