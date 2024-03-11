package com.abhinav.routes

import com.abhinav.data.note.Note
import com.abhinav.data.note.NoteDataSource
import com.abhinav.data.request.NoteIdReq
import com.abhinav.data.request.NoteRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.addNote(notesDataSource: NoteDataSource) {
    post("addNote") {
        val request = call.receiveNullable<NoteRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Bad Request")
            return@post
        }

        val id = ObjectId(request.objId)

        val wasAcknowledged =
            notesDataSource.insertNote(id, Note(request.date, request.title, request.content))
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, "Not ACK")
            return@post
        }

        call.respond(HttpStatusCode.Created, "Note created successfully")
    }
}

fun Route.deleteNote(noteDataSource: NoteDataSource) {
    delete("deleteNote/{id}") {
        val id = call.parameters["id"]
        if (id.isNullOrEmpty())
            call.respond(HttpStatusCode.BadRequest)

        val noteId = call.receiveNullable<NoteIdReq>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val objId = ObjectId(noteId.objId)

        val wasAcknowledged = noteDataSource.deleteNote(ObjectId(id),objId)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, "Not ACK")
            return@delete
        }

        call.respond(HttpStatusCode.OK, "Note deleted successfully")
    }
}

fun Route.updateNote(noteDataSource: NoteDataSource) {
    put("updateNote/{id}") {
        val id = call.parameters["id"]
        if (id.isNullOrEmpty())
            call.respond(HttpStatusCode.BadRequest)

        val request = call.receiveNullable<NoteRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Very BAd")
            return@put
        }

        val objId = ObjectId(id)
        val reqId = ObjectId(request.objId)

        val result = noteDataSource.updateNote(
            objId,
            Note(request.date, request.title, request.content, reqId)
        )
        if (result)
            call.respond(HttpStatusCode.OK, "Note updated successfully")
        else
            call.respond(HttpStatusCode.Conflict, "Incorrect details")
    }
}


fun Route.getNotesList(noteDataSource: NoteDataSource) {
    get("getNotesList/{id}") {
        val id = call.parameters["id"]
        if (id.isNullOrEmpty())
            call.respond(HttpStatusCode.BadRequest)

        val notesList = noteDataSource.getNotesList(ObjectId(id))
        if (notesList.isNullOrEmpty())
            call.respond(HttpStatusCode.NotFound, "Notes not found")
        else
            call.respond(HttpStatusCode.OK, notesList)
    }
}

fun Route.getNoteById(noteDataSource: NoteDataSource) {
    post("getNoteById/{id}") {
        val id = call.parameters["id"]
        if (id.isNullOrEmpty())
            call.respond(HttpStatusCode.BadRequest)

        val request = call.receiveNullable<NoteIdReq>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            val objId = ObjectId(id)

            val note = noteDataSource.getNoteById(objId,ObjectId(request.objId))

            if (note == null) {
                call.respond(HttpStatusCode.NotFound, "Not Found")
            } else {
                call.respond(HttpStatusCode.OK, note)
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
        }
    }
}