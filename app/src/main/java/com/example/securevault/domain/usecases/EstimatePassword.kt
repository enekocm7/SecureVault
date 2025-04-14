package com.example.securevault.domain.usecases

import com.example.securevault.domain.entities.PasswordStrength
import me.gosimple.nbvcxz.Nbvcxz

class EstimatePassword {
    val nbvcxz : Nbvcxz = Nbvcxz()

    operator fun invoke(password : String) : PasswordStrength{
        return PasswordStrength.fromScore(nbvcxz.estimate(password).basicScore)
    }
}