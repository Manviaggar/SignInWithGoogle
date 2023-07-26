package com.example.weatherhelp.signin

data class SignInResult(
    val data : UserData?,
    val errormessage:String?

)
data class UserData(
    val userid: String,
    val username:String?,
    val profilepictureurl:String?
)