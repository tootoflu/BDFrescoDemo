package com.bytedance.fresco.showsample

import android.content.Intent
import android.os.Bundle
import android.os.Handler


/**
 *
 *
 * @author lewin
 * @time 2020/6/1
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fresco_activity_splash)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}