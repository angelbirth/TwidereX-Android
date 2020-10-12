/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.utils

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.decodeFromJsonElement
import java.net.URLDecoder
import java.nio.charset.Charset

inline fun <reified T> String.queryString(charset: Charset = Charsets.UTF_8): T {
    val map = split("&")
        .map {
            it.split("=")
                .map {
                    URLDecoder.decode(it)
                }
                .let {
                    it[0] to it[1]
                }
        }
        .toMap()
    return JSON.encodeToJsonElement(MapSerializer(String.serializer(), String.serializer()), map).let {
        JSON.decodeFromJsonElement<T>(it)
    }
}
