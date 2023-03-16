/*
 * Copyright (C) 2023 BananaDroid
 * Copyright (C) 2023 Andrea Canale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bananadroid.updater.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.media.tv.TvContract.Programs
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class Downloader{
    // Indicate that we would like to update download progress
    private val UPDATE_DOWNLOAD_PROGRESS = 1
    public lateinit var progress_bar: ProgressBar;

    // Use a background thread to check the progress of downloading
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)

    // Use a hander to update progress bar on the main thread
    private val mainHandler: Handler = Handler(Looper.getMainLooper(), object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (msg.what == UPDATE_DOWNLOAD_PROGRESS) {
                val downloadProgress: Int = msg.arg1

                // Update your progress bar here.
                progress_bar.progress = downloadProgress
            }
            return true
        }

    })


    @SuppressLint("Range")
    public fun downloadFile(fileName : String, desc :String, url : String, context: Context, activity: Activity){
        // fileName -> fileName with extension

        activity.registerReceiver(attachmentDownloadCompleteReceive, IntentFilter(
            DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        );
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(fileName)
            .setDescription(desc)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
        val downloadManager= getSystemService(context, DownloadManager::class.java) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Run a task in a background thread to check download progress
        executor.execute {
            var progress = 0
            var isDownloadFinished = false
            while (!isDownloadFinished) {
                val cursor: Cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                if (cursor.moveToFirst()) {
                    val downloadStatus: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (downloadStatus) {
                        DownloadManager.STATUS_RUNNING -> {
                            val totalBytes: Long =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (totalBytes > 0) {
                                val downloadedBytes: Long =
                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                progress = (downloadedBytes * 100 / totalBytes).toInt()
                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100
                            isDownloadFinished = true
                        }
                        DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {}
                        DownloadManager.STATUS_FAILED -> isDownloadFinished = true
                    }
                    val message = Message.obtain()
                    message.what = UPDATE_DOWNLOAD_PROGRESS
                    message.arg1 = progress
                    mainHandler.sendMessage(message)
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun get_filename_from_dm(context: Context, downloadId: Long) : String? {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
        }
        cursor.close()
        return null
    }

    var attachmentDownloadCompleteReceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0
                )
                Toast.makeText(context, "Download completed. Starting update", Toast.LENGTH_LONG).show()
                val rom_zip = File(Environment.DIRECTORY_DOWNLOADS + get_filename_from_dm(context, downloadId))
                android.os.RecoverySystem.installPackage(context, rom_zip)
            }
        }
    }

}