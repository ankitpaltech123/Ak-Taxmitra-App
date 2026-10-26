package com.example.data.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

class VoiceAssistantManager(private val context: Context) {
    private val TAG = "VoiceAssistantManager"

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    var onSpeechRecognized: ((String) -> Unit)? = null

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Indian English first, then default US
                val result = textToSpeech?.setLanguage(Locale.forLanguageTag("en-IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.US)
                }
                textToSpeech?.setSpeechRate(1.0f)
                textToSpeech?.setPitch(1.05f)
                isTtsReady = true

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _voiceState.value = VoiceState.SPEAKING
                    }

                    override fun onDone(utteranceId: String?) {
                        _voiceState.value = VoiceState.IDLE
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _voiceState.value = VoiceState.IDLE
                    }
                })
            } else {
                Log.w(TAG, "TTS Initialization failed")
            }
        }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w(TAG, "SpeechRecognizer not available on device")
            _voiceState.value = VoiceState.LISTENING
            return
        }

        stopSpeaking()

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.LISTENING
                        _liveTranscript.value = "Listening..."
                    }

                    override fun onBeginningOfSpeech() {
                        _voiceState.value = VoiceState.LISTENING
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _audioRms.value = (rmsdB.coerceAtLeast(0f) / 10f).coerceIn(0f, 1f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.THINKING
                    }

                    override fun onError(error: Int) {
                        Log.w(TAG, "Speech recognition error code: $error")
                        _voiceState.value = VoiceState.IDLE
                        _liveTranscript.value = ""
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val spokenText = matches?.firstOrNull() ?: ""
                        if (spokenText.isNotBlank()) {
                            _liveTranscript.value = spokenText
                            _voiceState.value = VoiceState.THINKING
                            onSpeechRecognized?.invoke(spokenText)
                        } else {
                            _voiceState.value = VoiceState.IDLE
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _liveTranscript.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to start listening: ${e.message}")
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping listening: ${e.message}")
        }
        _voiceState.value = VoiceState.IDLE
    }

    fun speak(text: String) {
        if (isTtsReady && textToSpeech != null) {
            _voiceState.value = VoiceState.SPEAKING
            // Clean markdown formatting for clean audio utterance
            val cleanSpokenText = text
                .replace("*", "")
                .replace("#", "")
                .replace("`", "")
                .replace("•", " ")

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "taxmitra_voice_${System.currentTimeMillis()}")
            }
            textToSpeech?.speak(cleanSpokenText, TextToSpeech.QUEUE_FLUSH, params, "taxmitra_voice")
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping TTS: ${e.message}")
        }
        if (_voiceState.value == VoiceState.SPEAKING) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing voice manager: ${e.message}")
        }
    }
}
