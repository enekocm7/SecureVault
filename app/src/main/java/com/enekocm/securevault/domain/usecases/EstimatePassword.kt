package com.enekocm.securevault.domain.usecases

import com.enekocm.securevault.domain.model.PasswordStrength
import me.gosimple.nbvcxz.Nbvcxz
import javax.inject.Inject

class EstimatePassword @Inject constructor(private val nbvcxz: Nbvcxz) {

    operator fun invoke(password: String): PasswordStrength {
        return PasswordStrength.fromScore(nbvcxz.estimate(password).basicScore)
    }
}