package com.bytedance.fresco.showsample.case

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.bytedance.fresco.native_heif_encoder.HeifEncoder
import com.bytedance.fresco.showsample.BaseActivity
import com.bytedance.fresco.showsample.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import java.io.*

/**
 * Encode sample case.
 *
 * @author lewin
 * @time 2020/9/1
 */

class HeifEncodeActivity : BaseActivity() {

    private val mBitmapReference = Fresco.getImagePipelineFactory()
        .platformBitmapFactory.createBitmap(600, 600, Bitmap.Config.ARGB_8888)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heif_encode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initBitmap()
        findViewById<Button>(R.id.encode).setOnClickListener {
            encodeHEIF(mBitmapReference.get())
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

    private fun initBitmap() {
        val bitmap = mBitmapReference.get()
        bitmap.reconfigure(600, 600, Bitmap.Config.ARGB_8888)
        for (i in 0..299) {
            for (j in 0..299) {
                bitmap.setPixel(i, j, Color.BLUE)
            }
        }
        for (i in 300..599) {
            for (j in 0..299) {
                bitmap.setPixel(i, j, Color.RED)
            }
        }
        for (i in 0..299) {
            for (j in 300..599) {
                bitmap.setPixel(i, j, Color.YELLOW)
            }
        }
        for (i in 300..599) {
            for (j in 300..599) {
                bitmap.setPixel(i, j, Color.WHITE)
            }
        }
        findViewById<SimpleDraweeView>(R.id.encode_bitmap_drawee).setImageBitmap(bitmap)
    }

    private fun encodeHEIF(bitmap: Bitmap) {
        Thread {
            var outputStream: OutputStream? = null
            var costTime = 0L;
            try {
                outputStream = FileOutputStream(
                    this@HeifEncodeActivity.filesDir.toString() + "/test.heif"
                )
                val pixel = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(
                    pixel, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height
                )
                val startTime = System.currentTimeMillis()
                HeifEncoder.compressToHEIF(bitmap, 0, outputStream)
                costTime = System.currentTimeMillis() - startTime
            } catch (ignore: FileNotFoundException) {
            } catch (ignore: IOException) {
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (ignore: IOException) {
                    }
                }
            }
            Handler(Looper.getMainLooper())
                .post {
                    this@HeifEncodeActivity.loadTestHeif(costTime)
                }
        }.start()
    }

    private fun loadTestHeif(costTime: Long) {
        findViewById<SimpleDraweeView>(R.id.encode_heif_drawee).setImageURI(
            Uri.fromFile(
                File(
                    getFilesDir(), "/test.heif"
                )
            )
        )
        findViewById<TextView>(R.id.cost_time_view).text = "耗时：" + costTime + "ms"
    }
}