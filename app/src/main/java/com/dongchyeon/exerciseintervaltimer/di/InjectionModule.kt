package com.dongchyeon.exerciseintervaltimer.di

import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InjectionModule {
    @Singleton
    @Provides
    fun providesTimerRepository(): TimerRepository {
        return TimerRepository()
    }
}