package com.bytedance.fresco.showsample

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import android.widget.TextView
import com.bytedance.fresco.showsample.model.ImageUrlModel
import com.bytedance.fresco.showsample.model.ShowImageUtil
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.fresco_activity_detail.urlText

/**
 * Activity that shows the image and log information for static/Gif/Awebp/heic image page
 *
 * @author Chentao Zhou
 * @time 2020/11/12
 */
class FigureDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.fresco_activity_detail)
        initView()
    }

    private fun initView() {
        val figure = findViewById<SimpleDraweeView>(R.id.figureDetail)
        val TextView = findViewById<TextView>(R.id.figureDetailTextView)

        val url = intent.getStringExtra("url")
        val log = intent.getStringExtra("log")

        ShowImageUtil.showRawImage(this, url!!, figure)
        TextView.setText(
            log ?: "If you want to check the log, follow the guild：\n " +
            "1.Go to the setting page \n " +
            "2.Double click the [清除缓存] button \n " +
            "3. Back to the log page then you can see the newest log"
        )
        urlText.text = url
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

        fun goDetail(activity: FragmentActivity, url: String) {
            val logJson =
                ViewModelProviders.of(activity).get(ImageUrlModel::class.java).logData.value?.get(url)
            val intent = Intent(activity, FigureDetailActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("log", logJson?.toString(4))
            activity.startActivity(intent)
        }
    }
}

