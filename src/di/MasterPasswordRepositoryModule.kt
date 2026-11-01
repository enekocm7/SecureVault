package di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.repository.MasterPasswordRepositoryImpl
import data.storage.AppKeyStorage
import domain.repository.MasterPasswordRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MasterPasswordRepositoryModule {

    @Provides
    @Singleton
    fun provideMasterPasswordRepository(storage: AppKeyStorage): MasterPasswordRepository {
        return MasterPasswordRepositoryImpl(storage)
    }
}
