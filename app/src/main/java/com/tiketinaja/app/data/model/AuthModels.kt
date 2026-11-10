package com.tiketinaja.app.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String?
)

data class UpdatePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class DeleteAccountRequest(
    val password: String
)

data class SimpleMessageResponse(
    val success: Boolean? = null,
    val message: String? = null
)
