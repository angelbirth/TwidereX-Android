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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.twidere.services.http.MicroBlogException
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.di.inject
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.twitter.TwitterTweetsRepository
import com.twidere.twiderex.utils.show
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException
import java.io.IOException

class MediaViewModel(
    private val account: AccountDetails,
    private val statusKey: MicroBlogKey,
) : ViewModel() {
    private val inAppNotification: InAppNotification by inject()
    private val repository: TwitterTweetsRepository by inject {
        parametersOf(account.accountKey, account.service as LookupService)
    }
    private val statusRepository: StatusRepository by inject()
    val loading = MutableLiveData(false)
    val status = liveData {
        emitSource(statusRepository.loadLiveDataFromCache(statusKey, account.accountKey))
    }

    init {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                repository.loadTweetFromNetwork(statusKey.id)
            } catch (e: MicroBlogException) {
                e.show(inAppNotification)
            } catch (e: IOException) {
                e.message?.let { inAppNotification.show(it) }
            } catch (e: HttpException) {
                e.message?.let { inAppNotification.show(it) }
            }
            loading.postValue(false)
        }
    }
}
