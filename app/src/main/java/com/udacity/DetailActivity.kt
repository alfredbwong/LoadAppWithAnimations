package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_detail.view.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val titleOfDownload = intent.getStringExtra("download-title")
        val downloadStatus = intent.getStringExtra("download-status")
        Log.i("DetailActivity.onCreate", "titleOfDownload $titleOfDownload ,downloadStatus: $downloadStatus")
        binding.contentDetailInclude.downloadTitleValue.text = titleOfDownload
        if (downloadStatus != null) {
            binding.contentDetailInclude.downloadStatusValue.text = when (downloadStatus.toInt()) {
                8 -> getString(R.string.success_status_text)
                16 -> getString(R.string.failure_status_text)
                else -> getString(R.string.default_status_text)
            }
        } else {
            binding.contentDetailInclude.downloadStatusValue.text = getString(R.string.default_status_text)
        }
        val backButton = binding.contentDetailInclude.backButton
        val motionLayout = binding.contentDetailInclude.detailMotionLayout
        val intent = Intent(this, MainActivity::class.java)

        motionLayout.setTransitionListener( object: MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                Log.i("DetailActivity.transitionListener", "onTransitionStarted")
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                Log.i("DetailActivity.transitionListener", "onTransitionChange")
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                Log.i("DetailActivity.transitionListener", "onTransitionCompleted")

                startActivity(intent)
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                Log.i("DetailActivity.transitionListener", "onTransitionTrigger")

            }

        })
        backButton.setOnClickListener {

            motionLayout.transitionToEnd()


        }

    }

}

//fun downloadAnotherFile() {
//    Log.i("DetailActivity.backButtonOnClickListener", "I have been clicked.")
//    val intent = Intent(this, MainActivity::class.java)
//    startActivity(intent)
//}

//{
//    Log.i("DetailActivity.backButtonOnClickListener", "I have been clicked.")
//    val intent = Intent(this, MainActivity::class.java)
//    startActivity(intent)
//}
