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
package com.twidere.twiderex.di

import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.viewmodel.ComposeViewModel
import com.twidere.twiderex.viewmodel.DraftComposeViewModel
import com.twidere.twiderex.viewmodel.DraftItemViewModel
import com.twidere.twiderex.viewmodel.MediaViewModel
import com.twidere.twiderex.viewmodel.timeline.HomeTimelineViewModel
import com.twidere.twiderex.viewmodel.timeline.MentionsTimelineViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediaViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchUserViewModel
import com.twidere.twiderex.viewmodel.twitter.user.FollowersViewModel
import com.twidere.twiderex.viewmodel.twitter.user.FollowingViewModel
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (account: AccountDetails, statusKey: MicroBlogKey) ->
        MediaViewModel(
            account,
            statusKey
        )
    }
    viewModel { TwitterSignInViewModel() }
    viewModel { (draftId: String) -> DraftItemViewModel(draftId) }
    viewModel { (account: AccountDetails, draft: DbDraft) -> DraftComposeViewModel(account, draft) }
    viewModel { (account: AccountDetails, statusKey: MicroBlogKey?, composeType: ComposeType) ->
        ComposeViewModel(account, statusKey, composeType)
    }
    viewModel { (account: AccountDetails, screenName: String, host: String, initialUserKey: MicroBlogKey?) ->
        UserViewModel(
            account,
            screenName,
            host,
            initialUserKey
        )
    }
    viewModel { (account: AccountDetails, screenName: String, userKey: MicroBlogKey) ->
        UserTimelineViewModel(
            account,
            screenName,
            userKey
        )
    }
    viewModel { (account: AccountDetails, screenName: String, userKey: MicroBlogKey) ->
        UserMediaTimelineViewModel(
            account,
            screenName,
            userKey
        )
    }
    viewModel { (account: AccountDetails, screenName: String, userKey: MicroBlogKey) ->
        UserFavouriteTimelineViewModel(
            account,
            screenName,
            userKey
        )
    }
    viewModel { (account: AccountDetails, statusKey: MicroBlogKey) ->
        TwitterStatusViewModel(
            account,
            statusKey
        )
    }
    viewModel { (account: AccountDetails, userKey: MicroBlogKey) ->
        FollowingViewModel(
            account,
            userKey
        )
    }
    viewModel { (account: AccountDetails, userKey: MicroBlogKey) ->
        FollowersViewModel(
            account,
            userKey
        )
    }
    viewModel { (account: AccountDetails, keyword: String) ->
        TwitterSearchUserViewModel(
            account,
            keyword
        )
    }
    viewModel { (account: AccountDetails, keyword: String) ->
        TwitterSearchTweetsViewModel(
            account,
            keyword
        )
    }
    viewModel { (account: AccountDetails, keyword: String) ->
        TwitterSearchMediaViewModel(
            account,
            keyword
        )
    }
    viewModel { (account: AccountDetails) ->
        MentionsTimelineViewModel(
            account,
        )
    }
    viewModel { (account: AccountDetails) ->
        HomeTimelineViewModel(
            account,
        )
    }
}
