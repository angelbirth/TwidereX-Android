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
package com.twidere.twiderex.viewmodel

import android.Manifest.permission
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.work.WorkManager
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.di.inject
import com.twidere.twiderex.extensions.combineWith
import com.twidere.twiderex.extensions.getCachedLocation
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.worker.draft.SaveDraftWorker
import com.twitter.twittertext.Extractor
import org.koin.core.parameter.parametersOf
import java.util.UUID

class DraftItemViewModel(
    private val draftId: String,
) : ViewModel() {

    private val repository: DraftRepository by inject()

    val draft = liveData {
        repository.get(draftId)?.let {
            emit(it)
        }
    }
}

class DraftComposeViewModel(
    account: AccountDetails,
    draft: DbDraft,
) : ComposeViewModel(
    account,
    draft.statusKey,
    draft.composeType,
) {

    override val draftId: String = draft._id

    init {
        setText(draft.content)
        putImages(draft.media.map { Uri.parse(it) })
        excludedReplyUserIds.postValue(draft.excludedReplyUserIds ?: emptyList())
    }
}

open class ComposeViewModel(
    protected val account: AccountDetails,
    protected val statusKey: MicroBlogKey?,
    val composeType: ComposeType,
) : ViewModel(), LocationListener {

    protected val draftRepository: DraftRepository by inject()
    private val locationManager: LocationManager by inject()
    protected val composeAction: ComposeAction by inject()
    private val userRepository: UserRepository by inject {
        parametersOf(
            account.accountKey,
            account.service as LookupService,
            account.service as RelationshipService,
        )
    }
    private val workManager: WorkManager by inject()

    open val draftId: String = UUID.randomUUID().toString()

    protected val service by lazy {
        account.service as TwitterService
    }
    protected val statusRepository: StatusRepository by inject()

    val location = MutableLiveData<Location?>()
    val excludedReplyUserIds = MutableLiveData<List<String>>(emptyList())

    val replyToUserName = liveData {
        if (composeType == ComposeType.Reply && statusKey != null) {
            emitSource(
                status.map {
                    it?.let { status ->
                        Extractor().extractMentionedScreennames(
                            status.htmlText
                        ).filter { it != account.user.screenName }
                    } ?: run {
                        emptyList<String>()
                    }
                },
            )
        } else {
            emit(emptyList<String>())
        }
    }

    val replyToUser = liveData {
        emitSource(
            replyToUserName.switchMap {
                liveData {
                    if (it.isNotEmpty()) {
                        emit(userRepository.lookupUsersByName(it))
                    }
                }
            },
        )
    }
    val text = MutableLiveData("")
    val images = MutableLiveData<List<Uri>>(emptyList())
    val canSaveDraft =
        text.combineWith(images) { text, imgs -> !text.isNullOrEmpty() || !imgs.isNullOrEmpty() }
    val locationEnabled = MutableLiveData(false)
    val status = liveData {
        statusKey?.let {
            emitSource(statusRepository.loadLiveDataFromCache(it, account.accountKey))
        } ?: run {
            emit(null)
        }
    }

    fun setText(value: String) {
        text.postValue(value)
    }

    fun compose() {
        text.value?.let {
            composeAction.commit(
                account.accountKey,
                ComposeData(
                    content = it,
                    draftId = draftId,
                    images = images.value?.map { it.toString() } ?: emptyList(),
                    composeType = composeType,
                    statusKey = statusKey,
                    lat = location.value?.latitude,
                    long = location.value?.longitude,
                    excludedReplyUserIds = excludedReplyUserIds.value
                )
            )
        }
    }

    fun saveDraft() {
        text.value?.let { text ->
            workManager
                .beginWith(
                    SaveDraftWorker.create(
                        ComposeData(
                            content = text,
                            draftId = draftId,
                            images = images.value?.map { it.toString() } ?: emptyList(),
                            composeType = composeType,
                            statusKey = statusKey,
                            lat = location.value?.latitude,
                            long = location.value?.longitude,
                            excludedReplyUserIds = excludedReplyUserIds.value
                        )
                    )
                )
                .enqueue()
        }
    }

    fun putImages(value: List<Uri>) {
        images.value?.let {
            value + it
        }?.let {
            it.take(4)
        }?.let {
            images.postValue(it)
        }
    }

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    fun trackingLocation() {
        locationEnabled.postValue(true)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true) ?: return
        locationManager.requestLocationUpdates(provider, 0, 0f, this)
        locationManager.getCachedLocation()?.let {
            location.postValue(it)
        }
    }

    fun disableLocation() {
        location.postValue(null)
        locationEnabled.postValue(false)
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        this.location.postValue(location)
    }

    // compatibility fix for Api < 22
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onCleared() {
        if (locationEnabled.value == true) {
            locationManager.removeUpdates(this)
        }
    }

    fun removeImage(item: Uri) {
        images.value?.let {
            it - item
        }?.let {
            images.postValue(it)
        }
    }

    fun excludeReplyUser(user: UiUser) {
        excludedReplyUserIds.value?.let {
            excludedReplyUserIds.postValue(it + user.id)
        }
    }

    fun includeReplyUser(user: UiUser) {
        excludedReplyUserIds.value?.let {
            excludedReplyUserIds.postValue(it - user.id)
        }
    }
}
