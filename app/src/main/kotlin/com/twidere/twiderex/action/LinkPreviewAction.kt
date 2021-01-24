package com.twidere.twiderex.action

import androidx.collection.LruCache
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.twidere.twiderex.model.LinkPreviewData
import com.twidere.twiderex.ui.UiState
import com.twidere.twiderex.worker.LinkPreviewWorker

class LinkPreviewAction constructor(
    private val workManager: WorkManager
) {
    private val cacheData = LruCache<String, LinkPreviewData>(100)

    fun getOrCreate(url: String) = liveData {
        cacheData.get(url).takeIf {
            it != null
        }?.let {
            emit(UiState(data = it))
        } ?: run {
            val request = LinkPreviewWorker.create(url)
            workManager.enqueueUniqueWork(
                url,
                ExistingWorkPolicy.KEEP,
                request
            )
            emitSource(workManager.getWorkInfoByIdLiveData(request.id).map {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val data = LinkPreviewData.fromWorkData(it.outputData)
                        cacheData.put(url, data)
                        UiState(data = data)
                    }
                    WorkInfo.State.RUNNING -> {
                        UiState(loading = true)
                    }
                    WorkInfo.State.FAILED -> {
                        UiState(exception = Exception())
                    }
                    else -> {
                        UiState()
                    }
                }
            })
        }
    }
}