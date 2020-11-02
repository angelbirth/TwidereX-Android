/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.scenes

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Topic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.node.Ref
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.StatusLineComponent
import com.twidere.twiderex.component.TextInput
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.maxComposeTextLength
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.composeImageSize
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.utils.AmbientLauncher
import com.twidere.twiderex.viewmodel.ComposeViewModel
import kotlinx.coroutines.launch

enum class ComposeType {
    New,
    Reply,
    Quote,
}

@Composable
fun ComposeScene(type: ComposeType, statusId: String?) {
    if (type == ComposeType.New) {
        ComposeScene()
    }
}

@OptIn(ExperimentalLazyDsl::class, ExperimentalFocus::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeScene(status: UiStatus? = null, composeType: ComposeType = ComposeType.New) {
    val viewModel = navViewModel<ComposeViewModel>()
    val (text, setText) = remember { mutableStateOf("") }
    val images by viewModel.images.observeAsState(initial = emptyList())
    val account = AmbientActiveAccount.current
    val location by viewModel.location.observeAsState()
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
    val navController = AmbientNavController.current
    val keyboardController = remember { Ref<SoftwareKeyboardController>() }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (status == null) {
            0
        } else {
            1
        }
    )
    status?.also {
        if (listState.firstVisibleItemIndex == 0) {
            keyboardController.value?.hideSoftwareKeyboard()
        } else if (listState.firstVisibleItemIndex == 1) {
            keyboardController.value?.showSoftwareKeyboard()
        }
    }
    Scaffold(
        topBar = {
            AppBar(
                title = {
                    Text(
                        text = when (composeType) {
                            ComposeType.Reply -> "Reply"
                            ComposeType.Quote -> "Quote"
                            else -> "Compose"
                        }
                    )
                },
                navigationIcon = {
                    AppBarNavigationButton(icon = Icons.Default.Close)
                },
                actions = {
                    IconButton(
                        enabled = text.isNotEmpty(),
                        onClick = {
                            viewModel.compose(text, composeType, status)
                            navController.popBackStack()
                        }
                    ) {
                        Icon(asset = Icons.Default.Send)
                    }
                }
            )
        }
    ) {
        Column {
            LazyColumn(
                modifier = Modifier.weight(1F),
                state = listState,
            ) {
                status?.let { status ->
                    item {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colors.surface.withElevation())
                        ) {
                            StatusLineComponent(lineDown = true) {
                                TimelineStatusComponent(
                                    data = status,
                                    showActions = false,
                                )
                            }
                        }
                    }
                }
                item {
                    StatusLineComponent(
                        lineUp = status != null,
                    ) {
                        Row(
                            modifier = Modifier.fillParentMaxSize()
                                .padding(16.dp),
                        ) {
                            account?.let {
                                NetworkImage(
                                    url = it.user.profileImage,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .width(profileImageSize)
                                        .height(profileImageSize)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier.weight(1F)
                            ) {
                                TextInput(
                                    modifier = Modifier.align(Alignment.TopCenter),
                                    value = text,
                                    onValueChange = { setText(it) },
                                    autoFocus = true,
                                    onTextInputStarted = {
                                        keyboardController.value = it
                                    },
                                    onClicked = {
                                        // TODO: scroll lazyColumn
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (images.any()) {
                LazyRowForIndexed(
                    modifier = Modifier.padding(horizontal = standardPadding * 2),
                    items = images
                ) { index, item ->
                    ComposeImage(item)
                    if (index != images.lastIndex) {
                        Spacer(modifier = Modifier.width(standardPadding))
                    }
                }
                Spacer(modifier = Modifier.height(standardPadding * 2))
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = standardPadding * 2)
            ) {
                Box(
                    modifier = Modifier
                        .width(profileImageSize / 2)
                        .height(profileImageSize / 2),
                ) {
                    CircularProgressIndicator(
                        progress = 1f,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                    )
                    CircularProgressIndicator(
                        progress = text.length.toFloat() / maxComposeTextLength.toFloat(),
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                if (locationEnabled) {
                    location?.let {
                        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                            Row {
                                Icon(asset = Icons.Default.Place)
                                Text(text = "${it.latitude}, ${it.longitude}")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding * 2))
            Divider()
            ComposeActions()
        }
    }
}

@Composable
private fun ComposeActions() {
    val viewModel = navViewModel<ComposeViewModel>()
    val launcher = AmbientLauncher.current
    val scope = rememberCoroutineScope()
    Box {
        Row {
            IconButton(
                onClick = {
                    scope.launch {
                        val item = launcher.launchForResult(ActivityResultContracts.GetMultipleContents())
                    }
//                            openImagePicker()
                }
            ) {
                Icon(asset = Icons.Default.Camera)
            }
            IconButton(onClick = {}) {
                Icon(asset = Icons.Default.Gif)
            }
            IconButton(onClick = {}) {
                Icon(asset = Icons.Default.AlternateEmail)
            }
            IconButton(onClick = {}) {
                Icon(asset = Icons.Default.Topic)
            }
            IconButton(
                onClick = {
//                            if (locationEnabled) {
//                                disableLocation()
//                            } else {
//                                getOrRequestLocation()
//                            }
                },
            ) {
                Icon(asset = Icons.Default.MyLocation)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(asset = Icons.Default.Pages)
            }
        }
    }
}

@Composable
private fun ComposeImage(item: Uri) {
    val viewModel = navViewModel<ComposeViewModel>()
    var expanded by remember { mutableStateOf(false) }
    val image = @Composable {
        Box(
            modifier = Modifier
                .heightIn(max = composeImageSize)
                .aspectRatio(1F)
                .clickable(
                    onClick = {
                        expanded = true
                    }
                )
                .clip(MaterialTheme.shapes.small),
        ) {
            NetworkImage(url = item)
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        toggle = image,
    ) {
        DropdownMenuItem(
            onClick = {
                expanded = false
                viewModel.removeImage(item)
            }
        ) {
            Text("Remove")
        }
    }
}