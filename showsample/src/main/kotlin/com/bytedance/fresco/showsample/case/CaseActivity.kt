package com.bytedance.fresco.showsample.case

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import com.bytedance.fresco.showsample.BaseActivity
import com.bytedance.fresco.showsample.R

/**
 *
 *
 * @author lewin
 * @time 2020/9/1
 */
class CaseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.case_heif_encode).setOnClickListener {
            startActivity(Intent(this, HeifEncodeActivity::class.java))
        }

        findViewById<Button>(R.id.case_sr).setOnClickListener{
            startActivity(Intent(this, SuperResolutionActivity::class.java))
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
}