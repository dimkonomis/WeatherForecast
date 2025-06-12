package com.app.weatherforecast.core.utils

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UtilsModule {

    @Binds
    @Singleton
    fun bindSdkProvider(sdkProviderImpl: SdkProviderImpl): SdkProvider

    companion object {

        @Singleton
        @Provides
        fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl

    }

}