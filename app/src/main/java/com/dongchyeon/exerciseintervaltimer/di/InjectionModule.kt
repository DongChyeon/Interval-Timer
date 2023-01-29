package com.dongchyeon.exerciseintervaltimer.di

import android.content.Context
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InjectionModule {
    @Singleton
    @Provides
    fun providesTimerRepository(@ApplicationContext context: Context): TimerRepository {
        return TimerRepository(context)
    }
}