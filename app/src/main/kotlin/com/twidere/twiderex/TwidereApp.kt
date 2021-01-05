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
package com.twidere.twiderex

import android.app.Application
import androidx.work.WorkManager
import androidx.work.await
import com.twidere.twiderex.di.androidModule
import com.twidere.twiderex.di.preferenceModule
import com.twidere.twiderex.di.repositoryModule
import com.twidere.twiderex.di.twidereModule
import com.twidere.twiderex.di.viewModelModule
import com.twidere.twiderex.di.workerModule
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin

class TwidereApp : Application() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TwidereApp)
            workManagerFactory()
            modules(androidModule)
            modules(preferenceModule)
            modules(twidereModule)
            modules(repositoryModule)
            modules(viewModelModule)
            modules(workerModule)
        }
        cancelPendingWorkManager(this)
    }
}

/**
 * If there is a pending work because of previous crash we'd like it to not run.
 *
 */
private fun cancelPendingWorkManager(app: TwidereApp) {
    runBlocking {
        WorkManager.getInstance(app)
            .cancelAllWork()
            .result
            .await()
    }
}
