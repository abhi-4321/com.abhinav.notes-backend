package com.abhinav

import com.abhinav.data.note.NoteDataSourceImpl
import com.abhinav.plugins.configureMonitoring
import com.abhinav.plugins.configureRouting
import com.abhinav.plugins.configureSecurity
import com.abhinav.plugins.configureSerialization
import com.abhinav.security.hashing.SHA256HashingService
import com.abhinav.security.token.JwtTokenService
import com.abhinav.security.token.TokenConfig
import com.abhinav.data.user.UserDataSourceImpl
import io.ktor.server.application.*
import kotlinx.coroutines.DelicateCoroutinesApi
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.io.File

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@OptIn(DelicateCoroutinesApi::class)
fun Application.module() {

    val dbName = "notes-database"
    val connectionString = System.getenv("CON_STR")
    val db = KMongo.createClient(connectionString).coroutine.getDatabase(dbName)

    val userDataSource = UserDataSourceImpl(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        environment.config.property("jwt.issuer").getString(),
        environment.config.property("jwt.audience").getString(),
        365L*1000L*60L*60L*24L,
        System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val noteDataSource = NoteDataSourceImpl(db)

    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource,hashingService,tokenService,tokenConfig, noteDataSource)
}
