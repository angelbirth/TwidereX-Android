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
package com.twidere.twiderex.viewmodel.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.twidere.services.http.MicroBlogException
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.di.inject
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.show
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException
import java.io.IOException

class UserViewModel(
    private val account: AccountDetails,
    private val screenName: String,
    private val host: String,
    private val initialUserKey: MicroBlogKey?,
) : ViewModel() {

    private val repository: UserRepository by inject {
        parametersOf(account.accountKey, account.service as LookupService, account.service as RelationshipService)
    }
    private val inAppNotification: InAppNotification by inject()

    val userKey = MutableLiveData(initialUserKey)
    val refreshing = MutableLiveData(false)
    val loadingRelationship = MutableLiveData(false)
    val user = userKey.switchMap {
        if (it != null) {
            repository.getUserLiveData(it)
        } else {
            liveData<UiUser?> { emit(null) }
        }
    }
    val relationship = MutableLiveData<IRelationship>()
    val isMe = userKey.map {
        it == account.accountKey
    }

    fun refresh() = viewModelScope.launch {
        refreshing.postValue(true)
        try {
            val user = repository.lookupUserByName(screenName)
            if (initialUserKey != user.userKey) {
                userKey.postValue(user.userKey)
            }
        } catch (e: MicroBlogException) {
            e.show(inAppNotification)
        } catch (e: IOException) {
            e.message?.let { inAppNotification.show(it) }
        } catch (e: HttpException) {
            e.message?.let { inAppNotification.show(it) }
        }
        refreshing.postValue(false)
    }

    fun follow() = viewModelScope.launch {
        loadingRelationship.postValue(true)
        repository.follow(screenName)
        loadRelationShip()
    }

    fun unfollow() = viewModelScope.launch {
        loadingRelationship.postValue(true)
        repository.unfollow(screenName)
        loadRelationShip()
    }

    private fun loadRelationShip() = viewModelScope.launch {
        loadingRelationship.postValue(true)
        try {
            repository.showRelationship(screenName).let {
                relationship.postValue(it)
            }
        } catch (e: MicroBlogException) {
        } catch (e: IOException) {
        } catch (e: HttpException) {
        }
        loadingRelationship.postValue(false)
    }

    init {
        refresh()
        loadRelationShip()
    }
}
