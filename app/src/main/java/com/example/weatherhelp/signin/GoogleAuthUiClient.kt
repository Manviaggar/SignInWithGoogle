package com.example.weatherhelp.signin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.weatherhelp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private  val context:Context,
    private  val oneTapClient: SignInClient
) {
    private val auth=Firebase.auth

    suspend fun  signin(): IntentSender?{
        val result=try{
            oneTapClient.beginSignIn(
buildSigninRequest()
            ).await()
        }catch (e: Exception){
e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }
    suspend fun  Signinwithintent(intent: Intent):SignInResult{
        val credential=oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdtoken=credential.googleIdToken
        val googleCredentials=GoogleAuthProvider.getCredential(googleIdtoken,null)
        return try {
            val user =auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run{
                    UserData(
                        userid = uid,
                        username = displayName,
                        profilepictureurl = photoUrl?.toString()
                    )
                },
                errormessage = null
            )
        }catch (e:Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errormessage = e.message
            )
        }
    }
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
        fun getSignedInuser(): UserData? =auth.currentUser?.run{
            UserData(
                userid = uid,
                username = displayName,
                profilepictureurl = photoUrl?.toString()
            )
        }
    private  fun buildSigninRequest(): BeginSignInRequest{
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.defaultwe))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}