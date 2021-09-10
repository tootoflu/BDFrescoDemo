package com.bytedance.fresco.showsample

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import com.bytedance.fresco.showsample.model.ShowImageUtil
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Activity that shows the original size figure
 *
 * @author Chentao Zhou
 * @time 2020/11/12
 */
class BigFigureActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_big_figure)
        initView()
    }

    private fun initView() {
        val bigFigure = findViewById<SimpleDraweeView>(R.id.bigFigure)
        val url = intent.getStringExtra("url")
        val flag = intent.getStringExtra("flag")
        val context = this

        if (flag == "SR") {
            ShowImageUtil.showSrImage(context, url!!, bigFigure)
        } else {
            ShowImageUtil.showRawImage(context, url!!, bigFigure)
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

        fun goBigFigure(activity: FragmentActivity, url: String, flag: String) {
            val intent = Intent(activity, BigFigureActivity::class.java)
            intent.putExtra("flag", flag)    // Identify whether it is a normal image or a super image
            intent.putExtra("url", url)
            activity.startActivity(intent)
        }
    }
}
