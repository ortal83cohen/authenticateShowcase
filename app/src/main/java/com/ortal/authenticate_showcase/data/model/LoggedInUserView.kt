package com.ortal.authenticate_showcase.data.model

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val token: String,
    val isFromRemote: Boolean
)