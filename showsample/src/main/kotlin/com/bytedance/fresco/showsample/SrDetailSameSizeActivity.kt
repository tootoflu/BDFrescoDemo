package com.bytedance.fresco.showsample

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bytedance.fresco.showsample.model.ShowImageUtil
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Activity that shows the same size SRed and raw image in the same page
 *
 * @author Chentao Zhou
 * @time 2020/11/18
 */
class SrDetailSameSizeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_sr_detail_same_size)
        initView()
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

    private fun initView() {
        val rawDetailSameSizeFigure = findViewById<SimpleDraweeView>(R.id.rawDetailSameSizeFigure)
        val srDetailSameSizeFigure = findViewById<SimpleDraweeView>(R.id.srDetailSameSizeFigure)
        val url = intent.getStringExtra("url")
        val context = this

        ShowImageUtil.showRawImage(context, url!!, rawDetailSameSizeFigure)
        ShowImageUtil.showSrImage(context, url!!, srDetailSameSizeFigure)
    }

    companion object {

        fun goSrSameSizeDetail(activity: FragmentActivity, url: String) {
            val intent = Intent(activity, SrDetailSameSizeActivity::class.java)
            intent.putExtra("url", url)
            activity.startActivity(intent)
        }
    }
}
