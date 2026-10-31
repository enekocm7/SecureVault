package di

import data.repository.MasterPasswordRepositoryImpl
import data.storage.AppKeyStorage
import domain.repository.MasterPasswordRepository
import javax.inject.Singleton

object MasterPasswordRepositoryModule {

    @Singleton
    fun provideMasterPasswordRepository(storage: AppKeyStorage): MasterPasswordRepository {
        return MasterPasswordRepositoryImpl(storage)
    }
}