package de.madem.model.api

data class AccountRegistration(val username: String, val password: String, val mail: String)
data class AccountLoginRequest(val username: String, val password: String)
data class AccountLoginResponse(val token: String, val uid: Int)