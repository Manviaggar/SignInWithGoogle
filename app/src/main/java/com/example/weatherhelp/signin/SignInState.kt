package com.example.weatherhelp.signin

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
