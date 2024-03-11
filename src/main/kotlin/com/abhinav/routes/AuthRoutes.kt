package com.abhinav.routes

import com.abhinav.data.request.AuthRequest
import com.abhinav.data.response.AuthResponse
import com.abhinav.data.user.User
import com.abhinav.data.user.UserDataSource
import com.abhinav.security.hashing.HashingService
import com.abhinav.security.hashing.SaltedHash
import com.abhinav.security.token.TokenClaim
import com.abhinav.security.token.TokenConfig
import com.abhinav.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.addUser(hashingService: HashingService, userDataSource: UserDataSource) {
    post("signup") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPasswordShort = request.password.length < 8
        if (areFieldsBlank || isPasswordShort) {
            call.respond(HttpStatusCode.Conflict, "Too Short")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(ObjectId(), request.username, saltedHash.hash, saltedHash.salt, emptyList())
        val wasAcknowledged = userDataSource.insertUser(user)

        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, "Not ACK")
            return@post
        }
        call.respond(HttpStatusCode.Created, "User Created Successfully")
    }
}

fun Route.loginUser(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "User Not found")
            return@post
        }

        val isValidPassword = hashingService.verify(request.password, SaltedHash(user.password, user.salt))
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(tokenConfig, TokenClaim("userId", user._id.toString()))

        call.respond(HttpStatusCode.OK, AuthResponse(token))
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is : $userId")
        }
    }
}