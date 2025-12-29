package com.attri.WordOfDay.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.Locale

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
                Toast.makeText(context, "English voice data missing. Please install from settings.", Toast.LENGTH_LONG).show()
            } else {
                isInitialized = true
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TTS", "TTS not initialized")
            // Attempt to re-initialize if failed previously or not ready
             if (tts == null) {
                 tts = TextToSpeech(context, this)
             }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
