package com.bytedance.fresco.showsample.ttnet

//import android.content.Context
//import android.location.Address
//import com.bytedance.fresco.showsample.ShowSampleApplication
//import com.bytedance.ttnet.ITTNetDepend
////import com.ss.android.common.AppConsts
////import com.ss.android.common.util.NetworkUtils
//import org.json.JSONObject
//import java.util.*

/**
 * TTNet配置类
 *
 * @author lewin
 * @time 2019/2/17
 */
//class TTNetDepend : ITTNetDepend {
//    override fun getContext(): Context {
//        return ShowSampleApplication.getInstance()!!
//    }
//
//    override fun isCronetPluginInstalled(): Boolean {
//        return true
//    }
//
//    override fun mobOnEvent(context: Context, eventName: String, labelName: String, extraJson: JSONObject) { //do nothing
//    }
//
//    override fun onNetConfigUpdate(config: JSONObject, localData: Boolean) { //do nothing
//    }
//
//    override fun onAppConfigUpdated(context: Context, ext_json: JSONObject) { //do nothing
//    }
//
//    override fun getLocationAdress(context: Context): Address? { //do nothing
//        return null
//    }
//
//    @Throws(Exception::class)
//    override fun executeGet(maxLength: Int, url: String): String? {
//        NetworkUtils.executeGet(-1, url)
//        return null
//    }
//
//    override fun checkHttpRequestException(tr: Throwable, remoteIp: Array<String>): Int {
//        return 0
//    }
//
//    override fun monitorLogSend(logType: String, json: JSONObject) {}
//    override fun getProviderString(context: Context, key: String, defaultValue: String): String? {
//        return null
//    }
//
//    override fun getProviderInt(context: Context, key: String, defaultValue: Int): Int {
//        return 0
//    }
//
//    override fun saveMapToProvider(context: Context, map: Map<String?, *>?) {}
//    override fun getConfigServers(): Array<String> {
//        return arrayOf(
//                "dm.toutiao.com",
//                "dm.bytedance.com",
//                "dm.pstatp.com"
//        )
//    }
//
//    override fun getHostSuffix(): String {
//        return ".snssdk.com"
//    }
//
//    override fun getApiIHostPrefix(): String {
//        return "ib"
//    }
//
//    override fun getCdnHostSuffix(): String {
//        return ".pstatp.com"
//    }
//
//    override fun getHostReverseMap(): Map<String, String> {
//        val reverseMap: MutableMap<String, String> = LinkedHashMap()
//        reverseMap[AppConsts.API_HOST_I] = "i"
//        reverseMap[AppConsts.API_HOST_SI] = "si"
//        reverseMap[AppConsts.API_HOST_API] = "isub"
//        reverseMap[AppConsts.API_HOST_SRV] = "ichannel"
//        reverseMap[AppConsts.API_HOST_LOG] = "log"
//        reverseMap[AppConsts.API_HOST_MON] = "mon"
//        return reverseMap
//    }
//
//    override fun getShareCookieMainDomain(): String {
//        return ""
//    }
//}