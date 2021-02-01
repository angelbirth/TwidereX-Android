/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.services.mastodon.api

import com.twidere.services.mastodon.api.model.ExcludeTypes
import com.twidere.services.mastodon.model.Notification
import com.twidere.services.mastodon.model.Status
import retrofit2.http.GET
import retrofit2.http.Query

interface TimelineResources {
    @GET("/api/v1/timelines/home")
    suspend fun homeTimeline(
        @Query("max_id") max_id: String? = null,
        @Query("since_id") since_id: String? = null,
        @Query("min_id") min_id: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("local") local: Boolean? = null,
    ): List<Status>

    @JvmSuppressWildcards
    @GET("/api/v1/notifications")
    suspend fun notifications(
        @Query("max_id") max_id: String? = null,
        @Query("since_id") since_id: String? = null,
        @Query("min_id") min_id: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("exclude_types[]") exclude_types: List<ExcludeTypes>? = null,
        @Query("account_id") account_id: String? = null,
    ): List<Notification>
}
