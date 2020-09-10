package com.twidere.twiderex.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.twidere.twiderex.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SplashFragment : ComposeFragment() {
    @Composable
    override fun onCompose() {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalGravity = Alignment.CenterHorizontally
        ) {
            //TODO: replace with real icon
            Image(vectorResource(id = R.drawable.ic_launcher_foreground))
        }
        onActive(callback = {
            lifecycleScope.launchWhenCreated {
                delay(2000)
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_splash_fragment_to_twitter_sign_in_fragment)
                }
            }
        })
    }
}