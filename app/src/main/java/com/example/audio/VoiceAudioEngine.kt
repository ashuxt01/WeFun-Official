package com.example.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.ToneGenerator
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

enum class RtcEngineMode(val label: String) {
    DEMO_LOCAL_MIC("Local Mic VAD • Demo Room"),
    PERMISSION_REQUIRED("Mic Permission Needed"),
    REMOTE_UNCONFIGURED("Audio Muted • Demo Mode"),
}

data class AudioEngineState(
    val hasRecordPermission: Boolean = false,
    val isCapturingMic: Boolean = false,
    val micRmsLevel: Float = 0f,
    val isVoiceDetected: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val mode: RtcEngineMode = RtcEngineMode.REMOTE_UNCONFIGURED,
    val statusDetail: String = "Local mic VAD ready. Add TRTC/LiveKit credentials for multi-device RTC.",
)

/**
 * Real Android AudioRecord RMS Voice-Activity Engine + clean RTC gateway abstraction.
 * Captures live microphone audio when RECORD_AUDIO is granted and user is on an unmuted mic seat,
 * without ever pretending an unconfigured remote RTC server is connected.
 */
class VoiceAudioEngine(private val context: Context) {

    private val _state = MutableStateFlow(
        AudioEngineState(hasRecordPermission = checkRecordPermission())
    )
    val state: StateFlow<AudioEngineState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var captureJob: Job? = null
    private var musicJob: Job? = null

    fun checkRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun refreshPermission() {
        val granted = checkRecordPermission()
        _state.value = _state.value.copy(
            hasRecordPermission = granted,
            mode = if (granted) RtcEngineMode.REMOTE_UNCONFIGURED else RtcEngineMode.PERMISSION_REQUIRED
        )
    }

    fun startMicCapture() {
        val granted = checkRecordPermission()
        if (!granted) {
            _state.value = _state.value.copy(
                hasRecordPermission = false,
                isCapturingMic = false,
                micRmsLevel = 0f,
                isVoiceDetected = false,
                mode = RtcEngineMode.PERMISSION_REQUIRED,
                statusDetail = "Microphone permission required for live voice capture."
            )
            return
        }
        if (captureJob?.isActive == true) return

        captureJob = scope.launch {
            val sampleRate = 16000
            val minBuffer = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(1024)

            var recorder: AudioRecord? = null
            try {
                recorder = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBuffer
                )
                if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                    fallbackRmsMeter("Mic hardware unavailable on device; local meter paused.")
                    return@launch
                }
                recorder.startRecording()
                _state.value = _state.value.copy(
                    hasRecordPermission = true,
                    isCapturingMic = true,
                    mode = RtcEngineMode.DEMO_LOCAL_MIC,
                    statusDetail = "Live Mic VAD Active (Local AudioRecord) • Configure TRTC/LiveKit for remote RTC"
                )
                val buffer = ShortArray(minBuffer / 2)
                while (isActive) {
                    val read = recorder.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        var sumSquares = 0.0
                        for (i in 0 until read) {
                            val sample = buffer[i].toDouble()
                            sumSquares += sample * sample
                        }
                        val rms = sqrt(sumSquares / read)
                        val normalized = (rms / 6500.0).toFloat().coerceIn(0f, 1f)
                        _state.value = _state.value.copy(
                            micRmsLevel = normalized,
                            isVoiceDetected = normalized > 0.08f
                        )
                    } else {
                        delay(40)
                    }
                }
            } catch (_: SecurityException) {
                _state.value = _state.value.copy(
                    hasRecordPermission = false,
                    isCapturingMic = false,
                    mode = RtcEngineMode.PERMISSION_REQUIRED
                )
            } catch (_: Exception) {
                fallbackRmsMeter("Mic capture paused on this virtual device.")
            } finally {
                try {
                    recorder?.stop()
                } catch (_: Exception) {
                }
                try {
                    recorder?.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    private suspend fun fallbackRmsMeter(reason: String) {
        _state.value = _state.value.copy(
            isCapturingMic = false,
            micRmsLevel = 0f,
            isVoiceDetected = false,
            mode = RtcEngineMode.REMOTE_UNCONFIGURED,
            statusDetail = reason
        )
        while (captureJob?.isActive == true) {
            delay(500)
        }
    }

    fun stopMicCapture() {
        captureJob?.cancel()
        captureJob = null
        _state.value = _state.value.copy(
            isCapturingMic = false,
            micRmsLevel = 0f,
            isVoiceDetected = false,
            mode = if (checkRecordPermission()) RtcEngineMode.REMOTE_UNCONFIGURED else RtcEngineMode.PERMISSION_REQUIRED,
            statusDetail = "Mic muted • Configure TRTC/LiveKit credentials for multi-device RTC"
        )
    }

    fun toggleBackgroundMusic(play: Boolean) {
        if (!play) {
            musicJob?.cancel()
            musicJob = null
            _state.value = _state.value.copy(isMusicPlaying = false)
            return
        }
        if (musicJob?.isActive == true) return
        _state.value = _state.value.copy(isMusicPlaying = true)
        musicJob = scope.launch {
            var toneGen: ToneGenerator? = null
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 28)
                val notes = listOf(
                    ToneGenerator.TONE_DTMF_1,
                    ToneGenerator.TONE_DTMF_3,
                    ToneGenerator.TONE_DTMF_5,
                    ToneGenerator.TONE_DTMF_8,
                )
                var idx = 0
                while (isActive) {
                    toneGen.startTone(notes[idx % notes.size], 110)
                    idx++
                    delay(1200)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    toneGen?.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    fun release() {
        stopMicCapture()
        toggleBackgroundMusic(false)
    }
}
