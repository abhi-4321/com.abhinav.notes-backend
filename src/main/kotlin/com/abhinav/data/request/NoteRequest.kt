package com.abhinav.data.request

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val date: String,
    val title: String,
    val content: String,
    var objId: String? = null
)

@Serializable
data class NoteIdReq(
    val objId: String
)


