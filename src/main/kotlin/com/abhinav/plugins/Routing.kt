package com.abhinav.plugins

import com.abhinav.data.note.NoteDataSource
import com.abhinav.data.user.UserDataSource
import com.abhinav.routes.*
import com.abhinav.security.hashing.HashingService
import com.abhinav.security.token.TokenConfig
import com.abhinav.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(userDataSource: UserDataSource, hashingService: HashingService, tokenService: TokenService, tokenConfig: TokenConfig, noteDataSource: NoteDataSource) {
    routing {
        get("test") {
            call.respond("Hello")
        }

        addUser(hashingService,userDataSource)
        loginUser(userDataSource, hashingService, tokenService, tokenConfig)
        authenticate()
        getSecretInfo()
        addNote(noteDataSource)
        updateNote(noteDataSource)
        deleteNote(noteDataSource)
        getNotesList(noteDataSource)
        getNoteById(noteDataSource)
    }
}
