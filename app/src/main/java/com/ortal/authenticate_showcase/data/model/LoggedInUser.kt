package com.ortal.authenticate_showcase.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    var token: String,
    val displayName: String
)