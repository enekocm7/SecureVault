package com.enekocm.securevault.domain.model

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Initial : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}