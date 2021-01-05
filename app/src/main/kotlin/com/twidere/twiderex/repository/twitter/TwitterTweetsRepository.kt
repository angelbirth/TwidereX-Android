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
package com.twidere.twiderex.repository.twitter

import com.twidere.services.microblog.LookupService
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.di.inject
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi

class TwitterTweetsRepository(
    private val accountKey: MicroBlogKey,
    private val lookupService: LookupService,
) {
    private val database: AppDatabase by inject()

    suspend fun loadTweetFromNetwork(statusId: String): UiStatus {
        return toUiStatus(lookupService.lookupStatus(statusId) as StatusV2)
    }

    private suspend fun toUiStatus(status: StatusV2): UiStatus {
        val db = status.toDbTimeline(accountKey, TimelineType.Conversation)
        listOf(db).saveToDb(database)
        return db.toUi(accountKey)
    }
}
