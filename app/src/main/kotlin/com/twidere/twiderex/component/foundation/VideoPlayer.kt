/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.twidere.twiderex.utils.video.CacheDataSourceFactory
import com.twidere.twiderex.utils.video.VideoPool

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: PlayerControlView? = null,
    showControls: Boolean = customControl == null,
) {
    var autoPlay by savedInstanceState(url) { true }
    var window by savedInstanceState(url) { 0 }
    val context = AmbientContext.current
    val lifecycle = AmbientLifecycleOwner.current.lifecycle
    val player = remember(url) {
        SimpleExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = autoPlay
            setVolume(volume)
            ProgressiveMediaSource.Factory(
                CacheDataSourceFactory(
                    context,
                    5 * 1024 * 1024,
                )
            ).createMediaSource(MediaItem.fromUri(url)).also {
                setMediaSource(it)
            }
            prepare()
            seekTo(window, VideoPool.get(url))
        }
    }

    fun updateState() {
        autoPlay = player.playWhenReady
        window = player.currentWindowIndex
        VideoPool.set(url, 0L.coerceAtLeast(player.contentPosition))
    }

    val playerView = remember {
        StyledPlayerView(context).also { playerView ->
            playerView.useController = showControls
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    playerView.onResume()
                    player.playWhenReady = autoPlay
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                fun onStop() {
                    updateState()
                    playerView.onPause()
                    player.playWhenReady = false
                }
            })
        }
    }

    onActive {
        if (customControl != null) {
            customControl.player = player
        }
    }

    onDispose {
        updateState()
        player.release()
    }

    Box {
        AndroidView(
            modifier = modifier,
            viewBlock = { playerView }
        ) {
            playerView.player = player
        }
    }
}
