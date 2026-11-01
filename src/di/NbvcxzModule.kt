package di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.gosimple.nbvcxz.Nbvcxz
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NbvcxzModule {

    @Provides
    @Singleton
    fun provideNbvcxz(): Nbvcxz {
        return Nbvcxz()
    }
}
