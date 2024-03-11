package com.abhinav.data.user

import com.abhinav.data.note.Note
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId val _id: ObjectId = ObjectId(),
    val username: String,
    val password: String,
    val salt: String,
    val notes : List<Note>
)
