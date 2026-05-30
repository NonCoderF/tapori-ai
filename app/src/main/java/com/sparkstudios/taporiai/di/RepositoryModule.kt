package com.sparkstudios.taporiai.di

import com.sparkstudios.taporiai.repository.TaporiRepository
import com.sparkstudios.taporiai.repository.TaporiRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaporiRepository(
        taporiRepositoryImpl: TaporiRepositoryImpl
    ): TaporiRepository
}
