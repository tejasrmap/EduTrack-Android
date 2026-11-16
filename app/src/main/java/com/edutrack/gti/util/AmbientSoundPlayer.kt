package com.edutrack.gti.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * Real-time synthesized Ambient Sound Generator using Android AudioTrack API.
 * Generates soothing rain, forest breeze, white noise, and café acoustics locally.
 */
object AmbientSoundPlayer {
    private var audioTrack: AudioTrack? = null
    private var soundJob: Job? = null
    private var currentSoundName: String = "Mute"

    fun playSound(soundName: String) {
        stopSound()
        if (soundName == "Mute" || soundName.contains("Mute")) {
            currentSoundName = "Mute"
            return
        }

        currentSoundName = soundName
        val sampleRate = 44100
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            soundJob = CoroutineScope(Dispatchers.Default).launch {
                val bufferSize = minBufferSize / 2
                val buffer = ShortArray(bufferSize)

                var b0 = 0.0
                var b1 = 0.0
                var b2 = 0.0
                var b3 = 0.0
                var b4 = 0.0
                var b5 = 0.0
                var b6 = 0.0

                val isRain = soundName.contains("Rain")
                val isForest = soundName.contains("Forest")
                val isCafe = soundName.contains("Cafe")

                while (isActive) {
                    for (i in buffer.indices) {
                        val white = Random.nextDouble(-1.0, 1.0)
                        
                        // Pink / Rain noise synthesis algorithm
                        b0 = 0.99886 * b0 + white * 0.0555179
                        b1 = 0.99332 * b1 + white * 0.0750759
                        b2 = 0.96900 * b2 + white * 0.1538520
                        b3 = 0.86650 * b3 + white * 0.3104856
                        b4 = 0.55000 * b4 + white * 0.5329522
                        b5 = -0.7616 * b5 - white * 0.0168980
                        val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
                        b6 = white * 0.115926

                        val sampleValue = when {
                            isRain -> (pink * 1200.0).toInt().coerceIn(-32767, 32767)
                            isForest -> (pink * 800.0 + (if (Random.nextDouble() < 0.001) Random.nextDouble(-3000.0, 3000.0) else 0.0)).toInt().coerceIn(-32767, 32767)
                            isCafe -> (pink * 1000.0 + (if (Random.nextDouble() < 0.002) Random.nextDouble(-2000.0, 2000.0) else 0.0)).toInt().coerceIn(-32767, 32767)
                            else -> (white * 1800.0).toInt().coerceIn(-32767, 32767) // White Noise
                        }

                        buffer[i] = sampleValue.toShort()
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopSound() {
        soundJob?.cancel()
        soundJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
        currentSoundName = "Mute"
    }

    fun getCurrentSound(): String = currentSoundName
}
