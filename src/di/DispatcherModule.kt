package di

import javax.inject.Singleton

object DispatcherModule {

    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}