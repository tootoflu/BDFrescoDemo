package com.bytedance.fresco.showsample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bytedance.authorization.Authorization
import com.bytedance.authorization.AuthorizationResult
import kotlin.collections.Map.Entry

/**
 * Activity that shows the authorization information
 *
 * @author Chentao Zhou
 * @time 2020/11/12
 */
class AuthorizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_authorization)

        val authButton = findViewById<Button>(R.id.authButton)
        val authTextView = findViewById<TextView>(R.id.authTextView)
        authTextView.text = getAuthInfo()

        authButton.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("authorization", authTextView.text)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "已复制完成", Toast.LENGTH_SHORT).show()
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

    fun getAuthInfo(): String {
        val authorization: Authorization = Authorization.getInstance()
        val authResults : String? = getAllAuthorizations(authorization)
        if(authResults == null){
            return "There is no authorization information"
        }
        return authResults
    }



    /**
     * Get all authorization results information
     */
    fun getAllAuthorizations(authorization: Authorization): String? {
        val allResult: String? = getAllAuthorizationFromMemory(authorization)
        return if (allResult != null) {
            allResult
        } else {
            val response: String? = authorization.authorizationCache.getAuthorizationResponse()
            if (response == null) {
                return null
            } else {
                authorization.authorizationResult.replaceAll(authorization.checkAuthCodeList(response))
            }
            getAllAuthorizationFromMemory(authorization)
        }
    }

    /**
     * Get all authorization results from memory
     */
    private fun getAllAuthorizationFromMemory(authorization: Authorization): String? {
        var authInfo: StringBuilder? = null
        val entries: Iterator<Entry<String, AuthorizationResult>> = authorization.authorizationResult.entries.iterator()
        authInfo = StringBuilder()
        while (entries.hasNext()) {
            val entry = entries.next()
            authInfo.append(entry.value.decodedAuthCode)
            authInfo.append("\n\n"+"Authorization: ${entry.value.isOk} ${entry.value.failureReason}"+"\n")
            authInfo.append("————————————————————————————————————————————————————————————————")
        }
        return authInfo?.toString()
    }
}