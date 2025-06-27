package com.enekocm.securevault.domain.model

import com.google.firebase.firestore.Blob

data class Preferences(val key: Blob,val salt: Blob, val iv: Blob)