package com.abhinav.security.hashing

interface HashingService {
    fun generateSaltedHash(value: String, saltLength: Int = 32) : SaltedHash
    fun verify(value: String, saltedHash: SaltedHash) : Boolean
}