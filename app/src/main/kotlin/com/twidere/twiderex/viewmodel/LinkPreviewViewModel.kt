package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.action.LinkPreviewAction

class LinkPreviewViewModel @ViewModelInject constructor(
    private val linkPreviewAction: LinkPreviewAction,
) : ViewModel() {

}