package com.bytedance.fresco.showsample

import android.app.Application
import android.content.Context
import android.util.Log
import com.bytedance.cloudcontrol.CloudControl
import com.bytedance.fresco.avif.AvifDecoder
import com.bytedance.fresco.heif.HeifDecoder
import com.facebook.cache.common.BaseCacheEventListener
import com.facebook.cache.common.CacheEvent
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.cache.disk.DiskStorageCache
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ImageDecodeBitmapConfigStrategy
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.ImageDecoderConfig
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.imagepipeline.memory.PoolConfig
import com.facebook.imagepipeline.memory.PoolFactory
import com.facebook.net.FrescoTTNetFetcher
import com.facebook.net.RetryInterceptManager
import com.optimize.statistics.FrescoTraceListener
import com.optimize.statistics.InitConfig
import java.io.File
import java.util.ArrayList
import java.util.HashSet

/**
 *
 *
 * @author lewin
 * @time 2020/4/15
 */
object FrescoFactory {
    private const val KB = 1024
    private const val MB = 1024 * KB
    private const val IM_MAX_CACHE_SIZE = 40 * MB
    private const val LIVE_MAX_CACHE_SIZE = 30 * MB
    private const val IM_FRESCO_CACHE = "im_fresco_cache"
    private const val LIVE_FRESCO_CACHE = "live_fresco_cache"
    private const val LIVE_FRESCO_CONFIG = "live_fresco_config"

    fun init(context: Application, aid: String) {
        val initConfig = InitConfig(
            context,
            aid,
            "sample",
            "debug",
            "0.0.1",
            "1",
            "48144589260",
            InitConfig.CHINA,
            null,
            null
        )
        val ttNetFetcher = FrescoTTNetFetcher(initConfig)

        CloudControl.init(initConfig)

        val listeners: MutableSet<RequestListener> = HashSet()
        listeners.add(RequestLoggingListener())
        listeners.add(FrescoTraceListener(initConfig))
        val factory = PoolFactory(PoolConfig.newBuilder().build())
        val builder = ImagePipelineConfig.newBuilder(context)
            .setNetworkFetcher(ttNetFetcher)
            .setRequestListeners(listeners)
            .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
            .experiment()
            .setBitmapPrepareToDraw(true, 0, Int.MAX_VALUE, true)
            .setDownsampleEnabled(true)
            .setPoolFactory(factory)
            .setBitmapMemoryCacheTrimStrategy { 0.0 }
            .setImageDecoderConfig(
                ImageDecoderConfig.newBuilder().addDecodingCapability(
                    HeifDecoder.HEIF_FORMAT,
                    HeifDecoder.HeifFormatChecker(),
                    HeifDecoder.HeifFormatDecoder(factory.pooledByteBufferFactory)
                ).addDecodingCapability(
                    AvifDecoder.AVIF_FORMAT,
                    AvifDecoder.AvifFormatChecker(),
                    AvifDecoder.AvifFormatDecoder()
                ).addDecodingCapability(
                    AvifDecoder.AVIF_FORMAT_ANIMATED,
                    AvifDecoder.AvifFormatChecker(),
                    AvifDecoder.AvifFormatDecoder()
                ).build()
            )

        // add for disk cache md5
        val externalCacheDirAbsolutePath = context.externalCacheDir?.absolutePath; // /storage/emulated/0/Android/data/com.bytedance.fresco.showsample/cache
        val mainDiskCacheConfig = DiskCacheConfig.newBuilder(context)
            .setBaseDirectoryPath(File("$externalCacheDirAbsolutePath/静图/"))
            .setNeedMD5(true)
            .build()
        builder.setMainDiskCacheConfig(mainDiskCacheConfig);

        val smallImageDiskConfig = DiskCacheConfig.newBuilder(context)
            .setBaseDirectoryPath(File("$externalCacheDirAbsolutePath/GIF动图/"))
            .setNeedMD5(true)
            .build();
        builder.setSmallImageDiskCacheConfig(smallImageDiskConfig);

        builder.setCustomImageDiskCacheConfigMap(externalCacheDirAbsolutePath?.let {
            buildCustomDiskCacheConfigMap(context,
                it
            )
        })
        // end

        builder.experiment().setPieDecoderEnabled(true)
        ImageDecodeBitmapConfigStrategy.setStrategy(ImageDecodeBitmapConfigStrategy.DEFAULT)
        initProgressive(context)
        initFailRetry(context)
        Fresco.initialize(context, builder.build())

        (Fresco.getImagePipelineFactory().mainFileCache as DiskStorageCache).addCacheEventListener(
            object :
                BaseCacheEventListener() {
                override fun onWriteSuccess(cacheEvent: CacheEvent?) {
                    super.onWriteSuccess(cacheEvent)
                    Log.d(
                        "Fresco",
                        "CacheEventListener1 write success: " + cacheEvent!!.cacheKey.toString()
                    )
                }
            })

        (Fresco.getImagePipelineFactory().mainFileCache as DiskStorageCache).addCacheEventListener(
            object :
                BaseCacheEventListener() {
                override fun onWriteSuccess(cacheEvent: CacheEvent?) {
                    super.onWriteSuccess(cacheEvent)
                    Log.d(
                        "Fresco",
                        "CacheEventListener2 write success: " + cacheEvent!!.cacheKey.toString()
                    )
                }
            })
    }

    fun initProgressive(context: Context) {
        ImagePipelineConfig.getDefaultImageRequestConfig().isProgressiveRenderingEnabled = true
        ImagePipelineConfig.getDefaultImageRequestConfig().isProgressiveRenderingAnimatedEnabled = enableAnimatedProgressive(context)
        ImagePipelineConfig.getDefaultImageRequestConfig().isProgressiveRenderingHeicEnabled = true
    }

    fun initFailRetry(context: Context) {
        if (enableFailRetry(context)) {
            val connectTimeOuts = ArrayList<Int>()
            connectTimeOuts.add(3000)
            connectTimeOuts.add(5000)
            connectTimeOuts.add(15000)
            val readTimeOuts = ArrayList<Int>()
            readTimeOuts.add(5000)
            readTimeOuts.add(10000)
            readTimeOuts.add(20000)
            RetryInterceptManager.inst()
                .open(connectTimeOuts, readTimeOuts)
        } else {
            RetryInterceptManager.inst()
                .close()
        }
    }

    private fun buildCustomDiskCacheConfigMap(context: Context, externalCacheDirAbsolutePath: String) : HashMap<String, DiskCacheConfig> {
        val customDiskCacheConfigHashMap = HashMap<String, DiskCacheConfig>()
        val diskCacheConfig1 = DiskCacheConfig.newBuilder(context)
            .setBaseDirectoryPath(File("$externalCacheDirAbsolutePath/awebp/"))
            .setBaseDirectoryName(IM_FRESCO_CACHE)
            .setMaxCacheSize(IM_MAX_CACHE_SIZE.toLong())
            .setNeedMD5(true)
            .build()
        val diskCacheConfig2 = DiskCacheConfig.newBuilder(context)
            .setBaseDirectoryPath(File("$externalCacheDirAbsolutePath/heic/"))
            .setBaseDirectoryName(LIVE_FRESCO_CACHE)
            .setConfigBaseDirectoryPath(File("$externalCacheDirAbsolutePath/heic-config/"))
            .setConfigBaseDirectoryName(LIVE_FRESCO_CONFIG)
            .setNeedMD5(true)
            .setMaxCacheSize(LIVE_MAX_CACHE_SIZE.toLong())
            .build()
        return customDiskCacheConfigHashMap.apply {
            put(IM_FRESCO_CACHE, diskCacheConfig1)
            put(LIVE_FRESCO_CACHE, diskCacheConfig2)
        }
    }

}
