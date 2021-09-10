package com.bytedance.fresco.showsample

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import android.widget.Button
import com.bytedance.fresco.showsample.BigFigureActivity.Companion.goBigFigure
import com.bytedance.fresco.showsample.model.ImageUrlModel
import com.bytedance.fresco.showsample.model.ShowImageUtil
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.fresco_activity_detail.urlText

/**
 * Activity that shows the SRed and raw image in the same page
 *
 * @author ZhouChentao
 * @time 2020/11/12
 */
class SrDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.fresco_activity_sr_detail)
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

  //  @SuppressLint("CI_ByteDanceKotlinRules_Not_Allow_findViewById_Invoked_In_UI")
    private fun initView() {
        val rawDetailFigure = findViewById<SimpleDraweeView>(R.id.rawDetailFigure)
        val srDetailFigure = findViewById<SimpleDraweeView>(R.id.srDetailFigure)
        val url = intent.getStringExtra("url")
        val log = intent.getStringExtra("log")
        val context = this

        rawDetailFigure.setOnClickListener {
            goBigFigure(context, url!!, "normal")
        }

        srDetailFigure.setOnClickListener {
            goBigFigure(context, url!!, "SR")
        }

        //show the original image
        ShowImageUtil.showRawImage(context, url!!, rawDetailFigure)
        //show the SRed image
        ShowImageUtil.showSrImage(context, url!!, srDetailFigure)

        //transfer the log into logDetailActivity
        val checkLogButton = findViewById<Button>(R.id.checkLogButton)
        checkLogButton.setOnClickListener {
            LogDetailActivity.goLogDetailActivity(this, log)
        }

        val showSameSizeButton = findViewById<Button>(R.id.showSameSizeImageButton)
        showSameSizeButton.setOnClickListener {
            SrDetailSameSizeActivity.goSrSameSizeDetail(this, url)
        }
        urlText.text = url
    }

    companion object {

        fun goSrDetail(activity: FragmentActivity, url: String) {
            val logJson =
                ViewModelProviders.of(activity).get(ImageUrlModel::class.java).logData.value?.get(url)
            val intent = Intent(activity, SrDetailActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("log", logJson?.toString(4))
            activity.startActivity(intent)
        }
    }
}
