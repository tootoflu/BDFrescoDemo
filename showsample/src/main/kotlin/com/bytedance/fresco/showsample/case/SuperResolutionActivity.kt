package com.bytedance.fresco.showsample.case

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.bytedance.fresco.showsample.BaseActivity
import com.bytedance.fresco.showsample.R
import com.bytedance.fresco.sr.SRPostProcessor
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.activity_super_resolution.*

class SuperResolutionActivity : BaseActivity() {

    private var arrayUrl: Array<String> = arrayOf(
        "http://p6-tt.byteimg.com/tos-cn-p-0015/7f6e30df2d7c4801ad5c35c070ed252a~375x604_c1.jpeg",
        "http://p26-tt.byteimg.com/pgc-image/152618612482594d65f63cf~339x222_noop.image",
        "https://i.loli.net/2020/09/21/eX2R3ujyM1ISY9r.jpg"
    )
    private var srUrl = "https://i.loli.net/2020/08/31/1sOocAqxpbR3lCH.jpg"
    private lateinit var mSRPostProcessor: SRPostProcessor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_resolution)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mSRPostProcessor = SRPostProcessor()
        findViewById<Button>(R.id.fig1).setOnClickListener {
            showFigure(arrayUrl[0])
        }

        findViewById<Button>(R.id.fig2).setOnClickListener {
            showFigure(arrayUrl[1])
        }

        findViewById<Button>(R.id.fig3).setOnClickListener {
            showFigure(arrayUrl[2])
        }


        findViewById<Button>(R.id.VASR).setOnClickListener {
            showSRFigure()
        }
    }

    private fun showFigure(url: String) {
        //findViewById<SimpleDraweeView>(R.id.rawFigure).setImageURI(Uri.parse(url))
        val request = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(url))
            .build()

        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .build()
        rawFigure.controller = controller
        srUrl = url;
    }

    private fun showSRFigure() {

        if (srUrl == "") {
            Toast.makeText(applicationContext, "No picture selected", Toast.LENGTH_SHORT).show()
        }

        val request = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(srUrl))
            .setPostprocessor(mSRPostProcessor)
            .build()

        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .build()

        srFigure.controller = controller
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

    override fun onDestroy() {
        super.onDestroy()
        mSRPostProcessor.destroy()

    }


}