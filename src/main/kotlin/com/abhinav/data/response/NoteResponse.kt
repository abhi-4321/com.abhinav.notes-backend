package com.abhinav.data.response

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(
    val date: String,
    val title: String,
    val content: String
)