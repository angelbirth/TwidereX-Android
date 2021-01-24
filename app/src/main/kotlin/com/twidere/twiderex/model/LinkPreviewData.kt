package com.twidere.twiderex.model

import androidx.work.Data
import androidx.work.workDataOf
import com.twidere.twiderex.extensions.getNullableInt

data class LinkPreviewData(
    val title: String? = null,
    val desc: String? = null,
    val img: String? = null,
    val imgRes: Int? = null
) {
    companion object {
        fun fromWorkData(data: Data): LinkPreviewData {
            return LinkPreviewData(
                title = data.getString("title"),
                desc = data.getString("desc"),
                img = data.getString("img"),
                imgRes = data.getNullableInt("imgRes"),
            )
        }
    }

    fun toWorkData() = workDataOf(
        "title" to title,
        "desc" to desc,
        "img" to img,
        "imgRes" to imgRes,
    )
}