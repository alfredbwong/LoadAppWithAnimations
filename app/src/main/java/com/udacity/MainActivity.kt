package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.notification.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var binding:ActivityMainBinding

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private val noDownloadSelectedText = R.string.no_download_selected


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            //Check for radiobutton selected otherwise show toast
            val selectionId = radioGroup.checkedRadioButtonId
            binding.contentMain.customButton.isSelectionValid = true

            when (selectionId) {
                R.id.radioButtonGlide ->{
                    download(GLIDE_URL)
                }
                R.id.radioButtonLoadApp -> {
                    download(UDACITY_URL)
                }
                R.id.radioButtonRetrofit -> {
                    download(RETROFIT_URL)
                }
                -1 -> {
                    Toast.makeText(applicationContext, noDownloadSelectedText, Toast.LENGTH_SHORT).show()
                    binding.contentMain.customButton.isSelectionValid = false
                }
            }
            createChannel(
                    getString(R.string.notification_channel_id),
                    getString(R.string.notification_channel_name)
            )


        }


    }



    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("MainActivity", "Receiver: " + id.toString())
            val action = intent?.action
            Log.i("MainActivity", "Action intent: $action")

            //Query Download Manager for statusess
            val downloadManager : DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            if (downloadID == id){
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                Log.i("receiver", "id matches downloadId")

                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

                if (cursor.moveToFirst()) {
                    val titleOfDownload = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))

                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_FAILED -> {
                            Log.i("MainActivity", "Status STATUS_FAILED")
                            Toast.makeText(context, "Failed to download package", Toast.LENGTH_SHORT).show()
                            binding.contentMain.customButton.changeButtonState(ButtonState.Completed)
                            sendDownloadCompleteNotification(titleOfDownload, DownloadManager.STATUS_FAILED)

                        }
                        DownloadManager.STATUS_PAUSED -> {
                            Log.i("MainActivity", "Status STATUS_PAUSED")
                        }
                        DownloadManager.STATUS_PENDING -> {
                            Log.i("MainActivity", "Status STATUS_PENDING")
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            Log.i("MainActivity", "Status STATUS_RUNNING")
                            //This never comes up
                            binding.contentMain.customButton.changeButtonState(ButtonState.Loading)

                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.i("MainActivity", "Status STATUS_SUCCESSFUL")
                            binding.contentMain.customButton.changeButtonState(ButtonState.Completed)

                            sendDownloadCompleteNotification(titleOfDownload, DownloadManager.STATUS_SUCCESSFUL)
                        }
                        else ->{

                        }
                    }
                }
                cursor.close()

            }




        }
    }

    private fun download(url: String) {
        binding.contentMain.customButton.changeButtonState(ButtonState.Loading)

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

//        thread {
//            var downloading = true
//            while (downloading){
//                val query = DownloadManager.Query()
//                query.setFilterById(downloadID)
//
//                val cursor = downloadManager.query(query)
//                if(cursor.moveToFirst()){
//                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                    val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//
//                    if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL){
//                        downloading = false
//                    }
//                    var progress = 0L
//                    if (bytesTotal != -1){
//                        progress = bytesDownloaded*100L/bytesTotal
//
//                    }
//                    runOnUiThread {
//                        Log.i("MainActivity.checkdownload","bytesDownloaded : $bytesDownloaded bytesTotal $bytesTotal progress $progress" )
//
////                        Log.i("MainActivity.checkdownload",progress.toString())
//                    }
//
//                    cursor.close()
//                }
//            }
//        }


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

    private fun sendDownloadCompleteNotification(titleOfDownload: String, status: Int) {
        val notificationManager = ContextCompat.getSystemService(application, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(application.getString(R.string.notification_description), applicationContext, titleOfDownload, status)
    }

    private fun createChannel(channelId: String, channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Complete..."

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
