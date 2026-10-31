package utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.enekocm.securevault.R
import di.DefaultDispatcherProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoogleLogin(private val activity: AppCompatActivity) {
    private val dispatcherProvider = DefaultDispatcherProvider()
    private val auth: FirebaseAuth? = try { Firebase.auth } catch (_: Exception) { null }
    private val context = activity.baseContext
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    suspend fun signIn(allowNewAccounts : Boolean = true): AuthResult? {
        // Server client ID comes from google-services.json (google-services plugin).
        // Kargo does not apply that Gradle plugin, so provide the value via
        // `default_web_client_id` string resource (see res/values/strings.xml).
        val serverClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (_: Exception) {
            ""
        }
        if (serverClientId.isBlank() || serverClientId.startsWith("REPLACE")) {
            Toast.makeText(activity, "Google sign-in not configured", Toast.LENGTH_SHORT).show()
            return null
        }
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(!allowNewAccounts)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context,
            )
            return handleSignIn(result.credential)
        } catch (_: GetCredentialException) {
            return null
        }
    }

    private suspend fun handleSignIn(credential: Credential): AuthResult? {
        if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val firebaseAuth = auth ?: return null; return withContext(dispatcherProvider.io) {
                    firebaseAuth.signInWithCredential(credential).await()
                }
            } catch (_: GoogleIdTokenParsingException) {
                Toast.makeText(activity, "Failed to parse Google ID token", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(activity, "Unexpected credential type", Toast.LENGTH_SHORT).show()
        }
        return null
    }
}