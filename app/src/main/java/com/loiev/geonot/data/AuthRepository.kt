package com.loiev.geonot.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.loiev.geonot.R
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {

    private val auth = Firebase.auth

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun firebaseSignInWithGoogle(intent: Intent): AuthResult {
        val account = GoogleSignIn.getSignedInAccountFromIntent(intent).await()
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        return auth.signInWithCredential(credential).await()
    }

    suspend fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().await()
    }

    suspend fun firebaseSignUpWithEmail(email: String, password: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun firebaseSignInWithEmail(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }
    fun getUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun getUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    suspend fun updateUserProfile(displayName: String) {
        val profileUpdates = userProfileChangeRequest {
            this.displayName = displayName
        }
        auth.currentUser?.updateProfile(profileUpdates)?.await()
    }

    fun getCurrentUser() = auth.currentUser
}