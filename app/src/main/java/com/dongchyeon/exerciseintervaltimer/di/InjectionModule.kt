package com.dongchyeon.exerciseintervaltimer.di

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.speech.tts.TextToSpeech
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
    fun providesTTS(@ApplicationContext context: Context): TextToSpeech {
        var tts: TextToSpeech? = null

        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) tts?.language = Locale.KOREAN
        }

        return tts
    }
    @Singleton
    @Provides
    fun providesRingtone(@ApplicationContext context: Context): Ringtone {
        val notification: Uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
        return RingtoneManager.getRingtone(context, notification)
    }

    @Singleton
    @Provides
    fun providesTimerRepository(tts: TextToSpeech): TimerRepository {
        return TimerRepository(tts)
    }
}