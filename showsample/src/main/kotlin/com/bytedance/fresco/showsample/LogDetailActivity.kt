package com.bytedance.fresco.showsample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bytedance.fresco.showsample.R.layout

/**
 * Activity that shows log information for SRed image
 *
 * @author Chentao Zhou
 * @time 2020/11/12
 */
class LogDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.fresco_activity_log_detail)
        val detailLogText = findViewById<TextView>(R.id.detailLogText)
        val log = intent.getStringExtra("log")
        detailLogText.setText(log ?: "")

        val copyButton = findViewById<Button>(R.id.copyButton)
        copyButton.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("log_detail", detailLogText.text)
            clipboard!!.setPrimaryClip(clipData)
            Toast.makeText(this, "已复制至剪切板", Toast.LENGTH_SHORT).show()
        }
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

    companion object {

        fun goLogDetailActivity(context: Context, log: String?) {
            val intent = Intent(context, LogDetailActivity::class.java)
            intent.putExtra("log", log ?: "If you want to check the log, follow the guild：\n " +
                "1.Go to the setting page \n " +
                "2.Double click the [清除缓存] button \n " +
                "3. Back to the log page then you can see the newest log")
            context.startActivity(intent)
        }
    }
}