package com.bytedance.fresco.showsample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bytedance.cloudcontrol.CloudControl
import com.bytedance.fresco.showsample.R.layout
import kotlinx.android.synthetic.main.activity_cloud_control.cloudControltextView

/**
 * Activity that shows the cloud control information
 *
 * @author Chentao Zhou
 * @time 2020/11/12
 */
class CloudControlActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.activity_cloud_control)

        val cloudTextView = findViewById<TextView>(R.id.cloudControltextView)
        val cloudCopyButton = findViewById<Button>(R.id.cloudControlButton)
        cloudCopyButton.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("cloud_control_detail", cloudControltextView.text)
            clipboard!!.setPrimaryClip(clipData)
            Toast.makeText(this, "已复制至剪切板", Toast.LENGTH_SHORT).show()
        }
        cloudTextView.text = getCloudControlConfig()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getCloudControlConfig(): String {
        val cloudControl: CloudControl = CloudControl.getInstance()
        return cloudControl.controlCache.cloudControlConfig
    }
}