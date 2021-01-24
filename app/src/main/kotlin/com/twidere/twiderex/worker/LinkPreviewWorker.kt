package com.twidere.twiderex.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.twidere.twiderex.model.LinkPreviewData
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class LinkPreviewWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val client: OkHttpClient,
) : CoroutineWorker(appContext, params) {
    companion object {
        fun create(url: String) =
            OneTimeWorkRequestBuilder<LinkPreviewWorker>()
                .addTag(url)
                .setInputData(workDataOf("url" to url))
                .build()
    }

    override suspend fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()
        try {
            val response = client
                .newCall(
                    Request
                        .Builder()
                        .url(url.replace("http:", "https:"))
                        .build()
                )
                .execute()
            response.body?.string()?.let {
                Jsoup.parse(it)
            }?.let { doc ->
                val title = doc.getMeta("og:title") ?: doc.title()
                val desc = doc.getMeta("og:description")
                val img = doc.getMeta("og:image")
                LinkPreviewData(
                    title, desc, img
                )
            }?.let {
                return Result.success(it.toWorkData())
            }
        } catch (e: Throwable) {

        }
        return Result.failure()
    }

    private fun Document.getMeta(name: String): String? {
        return this.head().getElementsByAttributeValue("property", name)
            .firstOrNull { it.tagName() == "meta" }?.attributes()?.get("content")
    }
}