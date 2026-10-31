package domain.usecases

import domain.model.PasswordStrength
import me.gosimple.nbvcxz.Nbvcxz
import javax.inject.Inject

class EstimatePassword @Inject constructor(private val nbvcxz: Nbvcxz) {

    operator fun invoke(password: String): PasswordStrength {
        return PasswordStrength.fromScore(nbvcxz.estimate(password).basicScore)
    }
}