package com.enekocm.securevault.data.firestore

import com.google.firebase.firestore.Blob

data class FirestoreModel(
    val uid: String,
    var passwords: String,
    val derivedKey: Blob,
    val salt: Blob,
    val iv: Blob
)