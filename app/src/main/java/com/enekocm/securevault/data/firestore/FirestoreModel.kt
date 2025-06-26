package com.enekocm.securevault.data.firestore

data class FirestoreModel(
    val uid: String,
    var passwords: String,
    val derivedKey: String,
    val salt: String,
    val iv: String
)