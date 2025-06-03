package com.enekocm.securevault.data.autofill.entities

data class Credentials(val username: String?, val password: String?) {
    fun isValid(): Boolean {
        return !username.isNullOrBlank() && !password.isNullOrBlank()
    }
}
