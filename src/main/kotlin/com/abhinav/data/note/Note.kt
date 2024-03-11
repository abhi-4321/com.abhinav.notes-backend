package com.abhinav.data.note

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Note(
    val date: String,
    val title: String,
    val content: String,
    @BsonId val id : ObjectId = ObjectId()
)

