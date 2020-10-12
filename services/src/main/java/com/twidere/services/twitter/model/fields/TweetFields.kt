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
package com.twidere.services.twitter.model.fields

enum class TweetFields(val value: String) {
    attachments("attachments"),
    author_id("author_id"),
    context_annotations("context_annotations"),
    conversation_id("conversation_id"),
    created_at("created_at"),
    entities("entities"),
    geo("geo"),
    id("id"),
    in_reply_to_user_id("in_reply_to_user_id"),
    lang("lang"),
    non_public_metrics("non_public_metrics"),
    public_metrics("public_metrics"),
    organic_metrics("organic_metrics"),
    promoted_metrics("promoted_metrics"),
    possibly_sensitive("possibly_sensitive"),
    referenced_tweets("referenced_tweets"),
    source("source"),
    text("text"),
    withheld("withheld"),
}
