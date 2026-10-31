package di

import me.gosimple.nbvcxz.Nbvcxz
import javax.inject.Singleton

object NbvcxzModule {

    @Singleton

    fun provideNbvcxz(): Nbvcxz {
        return Nbvcxz()
    }
}