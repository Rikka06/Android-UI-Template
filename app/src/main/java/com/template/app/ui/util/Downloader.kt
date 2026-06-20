package com.template.app.ui.util

import android.net.Uri
import com.template.app.ui.util.module.LatestVersionInfo

suspend fun download(
    url: String,
    fileName: String,
    onDownloaded: (Uri) -> Unit = {},
    onDownloading: () -> Unit = {},
    onProgress: (Int) -> Unit = {}
) {
    onDownloading()
    onProgress(100)
    onDownloaded(Uri.parse("content://com.template.app.template/mock/$fileName"))
}

fun checkNewVersion(): LatestVersionInfo = LatestVersionInfo()

