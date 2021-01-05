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

import android.accounts.AccountManager
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.room.Room
import androidx.work.WorkManager
import com.twidere.twiderex.db.AppDatabase
import org.koin.dsl.module

val androidModule = module {
    factory { WorkManager.getInstance(get()) }
    factory { AccountManager.get(get()) }
    factory { get<Context>().getSharedPreferences("twiderex", Context.MODE_PRIVATE) }
    single { Room.databaseBuilder(get(), AppDatabase::class.java, "twiderex-db").build() }
    factory { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    factory { get<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
}
