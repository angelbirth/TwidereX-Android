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

import com.twidere.twiderex.worker.NotificationWorker
import com.twidere.twiderex.worker.TwitterComposeWorker
import com.twidere.twiderex.worker.draft.RemoveDraftWorker
import com.twidere.twiderex.worker.draft.SaveDraftWorker
import com.twidere.twiderex.worker.status.LikeWorker
import com.twidere.twiderex.worker.status.RetweetWorker
import com.twidere.twiderex.worker.status.UnLikeWorker
import com.twidere.twiderex.worker.status.UnRetweetWorker
import com.twidere.twiderex.worker.status.UpdateStatusWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { RemoveDraftWorker(get(), get(), get()) }
    worker { SaveDraftWorker(get(), get(), get()) }
    worker { LikeWorker(get(), get(), get(), get()) }
    worker { RetweetWorker(get(), get(), get(), get()) }
    worker { UnLikeWorker(get(), get(), get(), get()) }
    worker { UnRetweetWorker(get(), get(), get(), get()) }
    worker { UpdateStatusWorker(get(), get(), get(), get()) }
    worker { NotificationWorker(get(), get(), get()) }
    worker { TwitterComposeWorker(get(), get(), get(), get()) }
}
