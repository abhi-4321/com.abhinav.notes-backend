package com.abhinav.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
