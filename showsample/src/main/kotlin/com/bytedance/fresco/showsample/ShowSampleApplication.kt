package com.bytedance.fresco.showsample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import com.bytedance.applog.AppLog
import com.bytedance.applog.InitConfig
import com.bytedance.applog.util.UriConstants
import com.facebook.common.logging.FLog
import com.facebook.imagepipeline.memory.BitmapCounterConfig
import com.facebook.imagepipeline.memory.BitmapCounterProvider
import com.optimize.statistics.FrescoMonitor
import com.optimize.statistics.FrescoMonitorConst
import com.optimize.statistics.IMonitorHook
import com.optimize.statistics.IMonitorHookV2
import com.optimize.statistics.ImageTraceListener
import org.json.JSONObject
import java.util.HashMap
import kotlin.math.abs

/**
 *
 *
 * @author lewin
 * @time 2020/4/14
 */
class ShowSampleApplication : Application() {

    @SuppressLint("CI_StaticFieldLeak", "StaticFieldLeak")
    companion object {

        const val AID = "AppID"
        const val UUID = "UUID"
        const val TAG = "ShowSampleApplication"

        protected var sInstance: Application? = null
        private val imageLogListeners: MutableList<ImageLogListener> = ArrayList()


        fun getInstance(): Application? {
            return sInstance
        }

        @Synchronized
        fun addImageLogListener(listener: ImageLogListener) {
            imageLogListeners.add(listener)
        }

        @Synchronized
        fun removeImageLogListener(listener: ImageLogListener) {
            imageLogListeners.remove(listener)
        }

        fun getAppSharedPreferences(): SharedPreferences {
            return sInstance!!.getSharedPreferences(
                TAG,
                Context.MODE_PRIVATE
            )
        }

        @Synchronized
        private fun notifyImageLog(
            isSucceed: Boolean,
            requestId: String?,
            jsonObject: JSONObject?
        ) {
            imageLogListeners.forEach {
                it.onImageLoad(isSucceed, requestId, jsonObject)
            }
        }

    }

    interface ImageLogListener {
        fun onImageLoad(isSucceed: Boolean, requestId: String?, jsonObject: JSONObject?)
    }


    override fun onCreate() {
        super.onCreate()

        val aid = getAppSharedPreferences().getString(AID, "0")!!
        var uuid = getAppSharedPreferences().getString(UUID, "")
        if (TextUtils.isEmpty(uuid)) {
            uuid = abs(java.util.UUID.randomUUID().toString().hashCode()).toString()
            getAppSharedPreferences()
                .edit()
                .putString(UUID, uuid)
                .apply()
        }
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)
//        initTTNet()
        initAppLog(aid, uuid!!)
        FrescoFactory.init(this, aid)
        BitmapCounterProvider.initialize(
            BitmapCounterConfig.newBuilder()
                .setMaxBitmapCount(BitmapCounterConfig.DEFAULT_MAX_BITMAP_COUNT)
                .build()
        )
        initMonitor()

        FLog.isLoggable(FLog.DEBUG)
//        FLog.setLoggingDelegate(mLoggingDelegate)
//        ALogService.setAlogService(mALogService)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        sInstance = this
    }

    val TAG_MONITOR = "FrescoMonitor"

    private fun initMonitor() {
        FrescoMonitor.addImageTraceListener(object : ImageTraceListener {
            override fun onImageLoaded(isSucceed: Boolean, requestId: String?, jsonObject: JSONObject?) { //通过jsonObject参数是否含有IS_NETWORK_DOWNLOAD字段来判断图片请求是否经过网络
                //仅上报经过网络请求的图片加载结果，如果从缓存中拿到图片也想上报信息，则FrescoMonitor.setReportHitCacheEnabled(true)
                // MonitorUtils.monitorCommonLog(FrescoMonitorConst.MONITOR_IMAGE_V2, jsonObject);
                FLog.d(TAG_MONITOR, "onImageLoaded : $jsonObject")
                notifyImageLog(isSucceed, requestId, jsonObject)
            }
        })

        FrescoMonitor.addMonitorHookV2(IMonitorHookV2 { _, _, _, monitorData, _, _ ->
            val hashMap: MutableMap<String, Any> = HashMap()
            // 业务标示、场景标示
            hashMap["biz_tag"] = "toutiao"
            hashMap["scene_tag"] = "feed"
            if (monitorData != null) { // 过滤只有通过网络下载的才进行上报
                val isNetwork = monitorData.optBoolean(FrescoMonitorConst.IS_NETWORK_DOWNLOAD)
                return@IMonitorHookV2 Pair<Boolean, Map<String, Any>>(isNetwork, hashMap)
            }
            null
        })
    }

    private fun initAppLog(aid: String, uuid: String) {


        val config =
            InitConfig(aid!!, "debug") // appid和渠道，appid如不清楚请联系客户成功经理


        //上报域名默认为中国，可根据业务情况自己设置上报域名，新加坡：SINGAPORE；美东：AMERICA
        //上报域名默认为中国，可根据业务情况自己设置上报域名，新加坡：SINGAPORE；美东：AMERICA
        config.setUriConfig(UriConstants.REGION_DEFAULT)
        // 是否在控制台输出日志，可用于观察用户行为日志上报情况

        // 是否在控制台输出日志，可用于观察用户行为日志上报情况
        config.setLogger(com.bytedance.applog.ILogger { msg: String?, t: Throwable? ->
            Log.d(TAG, msg, t)
        })
        // 开启圈选埋点

        // 开启AB测试
        config.setAbEnable(true)

        config.setAutoStart(true)
        AppLog.init(this, config)
        /* 初始化结束 */

        /* 自定义 “用户公共属性”（可选，初始化后调用, key相同会覆盖）
      	关于自定义 “用户公共属性” 请注意：1. 上报机制是随着每一次日志发送进行提交，默认的日志发送频率是1分钟，所以如果在一分钟内连续修改自定义用户公共属性，，按照日志发送前的最后一次修改为准， 2. 不推荐高频次修改，如每秒修改一次 */
        /* 初始化结束 */

        /* 自定义 “用户公共属性”（可选，初始化后调用, key相同会覆盖）
      	关于自定义 “用户公共属性” 请注意：1. 上报机制是随着每一次日志发送进行提交，默认的日志发送频率是1分钟，所以如果在一分钟内连续修改自定义用户公共属性，，按照日志发送前的最后一次修改为准， 2. 不推荐高频次修改，如每秒修改一次 */
        val headerMap: MutableMap<String, Any> =
            HashMap()
        headerMap["level"] = 8
        headerMap["gender"] = "female"
        AppLog.setHeaderInfo(headerMap as HashMap<String, Any>)
        AppLog.setUserUniqueID(uuid)
    }
}