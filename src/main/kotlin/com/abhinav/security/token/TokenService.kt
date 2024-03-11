package com.abhinav.security.token

interface TokenService {
    fun generate(tokenConfig: TokenConfig, vararg tokenClaims: TokenClaim) : String
}