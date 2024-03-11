package com.abhinav.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(db: CoroutineDatabase) : UserDataSource {

    private val user = db.getCollection<User>("user")

    override suspend fun insertUser(user: User): Boolean {
        return this.user.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return user.findOne(User::username eq username)
    }
}