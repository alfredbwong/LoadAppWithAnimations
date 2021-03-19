package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private val noDownloadSelectedText = R.string.no_download_selected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            //Check for radiobutton selected otherwise show toast
            val selectionId = radioGroup.checkedRadioButtonId
            if (selectionId == -1) {
                Toast.makeText(applicationContext, noDownloadSelectedText, Toast.LENGTH_SHORT).show()
            } else {
//                when (selectionId) {
//                    R.id.radioButtonGlide -> download(GLIDE_URL)
//                    R.id.radioButtonLoadApp -> download(UDACITY_URL)
//                    R.id.radioButtonRetrofit -> download(RETROFIT_URL)
//                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("MainActivity", "Receiver: " + id.toString())
            val action = intent?.action
            Log.i("MainActivity", "Action intent: $action")

        }
    }

    private fun download(url: String) {
        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(when (url) {
                            UDACITY_URL -> "Load-App-Starter"
                            GLIDE_URL -> "Glide-Master"
                            RETROFIT_URL -> "Retrofit-Master"
                            else -> "Unknown-File"
                        })
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "starter.zip")

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val UDACITY_URL =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
                "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
                "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"

        private const val DEFAULT_UDACITY_FILE_NAME = "load-app"
        private const val DEAFULT_GLIDE_FILE_NAME = "glide"
        private const val DEFAULT_RETROFIT_FILE_NAME = "retrofit"
    }

}
