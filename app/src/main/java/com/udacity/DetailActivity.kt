package com.udacity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_detail.view.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val titleOfDownload = intent.getStringExtra("download-title")
        val downloadStatus = intent.getStringExtra("download-status")
        Log.i("DetailActivity.onCreate", "titleOfDownload $titleOfDownload ,downloadStatus: $downloadStatus")
        binding.contentDetailInclude.downloadTitle.text = titleOfDownload
        binding.contentDetailInclude.downloadStatus.text = downloadStatus
    }

}

