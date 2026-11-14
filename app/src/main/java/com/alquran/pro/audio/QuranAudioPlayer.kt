package com.alquran.pro.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurahNomor: Int? = null,
    val currentAyatNomor: Int? = null,
    val title: String = "",
    val subtitle: String = "",
    val errorMessage: String? = null
)

@OptIn(UnstableApi::class)
class QuranAudioPlayer(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playerState.update { it.copy(isBuffering = true) }
                    }
                    Player.STATE_READY -> {
                        _playerState.update {
                            it.copy(
                                isBuffering = false,
                                isPlaying = exoPlayer.isPlaying,
                                errorMessage = null
                            )
                        }
                    }
                    Player.STATE_ENDED -> {
                        _playerState.update {
                            it.copy(
                                isPlaying = false,
                                isBuffering = false
                            )
                        }
                    }
                    Player.STATE_IDLE -> {
                        _playerState.update {
                            it.copy(
                                isPlaying = false,
                                isBuffering = false
                            )
                        }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playerState.update {
                    it.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Gagal memutar audio: ${error.errorCodeName}"
                    )
                }
            }
        })
    }

    fun playVerse(surahNomor: Int, ayatNomor: Int, surahName: String, audioUrl: String) {
        if (audioUrl.isBlank()) return

        // Jika ayat yang sama sedang diputar, toggle pause/play
        val current = _playerState.value
        if (current.currentSurahNomor == surahNomor && current.currentAyatNomor == ayatNomor) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            return
        }

        _playerState.update {
            it.copy(
                isBuffering = true,
                currentSurahNomor = surahNomor,
                currentAyatNomor = ayatNomor,
                title = "$surahName : Ayat $ayatNomor",
                subtitle = "Tilawah Murottal",
                errorMessage = null
            )
        }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        val mediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun playFullSurah(surahNomor: Int, surahName: String, audioUrl: String) {
        if (audioUrl.isBlank()) return

        val current = _playerState.value
        if (current.currentSurahNomor == surahNomor && current.currentAyatNomor == null) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            return
        }

        _playerState.update {
            it.copy(
                isBuffering = true,
                currentSurahNomor = surahNomor,
                currentAyatNomor = null,
                title = "Surat $surahName",
                subtitle = "Audio Penuh Surat",
                errorMessage = null
            )
        }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        val mediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    fun resume() {
        if (!exoPlayer.isPlaying && exoPlayer.playbackState != Player.STATE_IDLE) {
            exoPlayer.play()
        }
    }

    fun stop() {
        exoPlayer.stop()
        _playerState.update {
            AudioPlayerState()
        }
    }

    fun release() {
        exoPlayer.release()
    }
}
