package com.example.core.security

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Handles Firebase Authentication via Jetpack Credential Manager (Google Sign-In).
 */
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
) {

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun currentUserId(): String? = auth.currentUser?.uid

    /**
     * Attempts silent automatic sign-in for previously authorized accounts on launch.
     * Never falls back to anonymous login or credential prompts when none exist.
     */
    suspend fun attemptAutoSignIn(context: Context): Result<FirebaseUser?> {
        auth.currentUser?.let { return Result.success(it) }

        val webClientId = resolveWebClientId(context)
        if (webClientId.isBlank()) {
            return Result.success(null)
        }

        val credentialManager = CredentialManager.create(context.applicationContext)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val user = exchangeCredential(result.credential)
            Result.success(user)
        } catch (_: NoCredentialException) {
            Result.success(null)
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Silent Google auto sign-in cancelled", e)
            Result.success(null)
        } catch (e: Exception) {
            Log.w(TAG, "Silent Google auto sign-in skipped", e)
            Result.success(null)
        }
    }

    /**
     * Launches the interactive Google Sign-In bottom sheet via Credential Manager.
     */
    suspend fun onGoogleSignInClicked(activityContext: Context): Result<FirebaseUser> {
        val webClientId = resolveWebClientId(activityContext)
        if (webClientId.isBlank()) {
            return Result.failure(
                IllegalStateException("Google OAuth web client ID is not configured.")
            )
        }

        val credentialManager = CredentialManager.create(activityContext)
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = webClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        return try {
            val result = credentialManager.getCredential(activityContext, request)
            val user = exchangeCredential(result.credential)
                ?: return Result.failure(IllegalStateException("Unsupported credential type returned."))
            Result.success(user)
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "User cancelled Google sign-in", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(context: Context) {
        val credentialManager = CredentialManager.create(context.applicationContext)
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "Unable to clear Credential Manager state", e)
            } finally {
                auth.signOut()
            }
        }
    }

    private suspend fun exchangeCredential(credential: androidx.credentials.Credential): FirebaseUser? {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCred = GoogleAuthProvider.getCredential(googleCred.idToken, null)
            val authResult = auth.signInWithCredential(firebaseCred).await()
            return authResult.user
        }
        return null
    }

    private fun resolveWebClientId(context: Context): String {
        val appContext = context.applicationContext
        val resId = appContext.resources.getIdentifier(
            "default_web_client_id",
            "string",
            appContext.packageName
        )
        return if (resId != 0) appContext.getString(resId) else ""
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
