package com.bytedance.fresco.showsample.model

import android.content.Context
import android.net.Uri
import com.bytedance.fresco.showsample.enableAnimatedAutoPlay
import com.bytedance.fresco.showsample.enableIgnoreDiskCache
import com.bytedance.fresco.showsample.enableIgnoreMemoryCache
//import com.bytedance.fresco.sr.SRPostProcessor
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * @author Chentao Zhou
 * @time 2020/11/18
 */
class ShowImageUtil {

    companion object {

        /**
         * the code to show the original image
         * */
        fun showRawImage(context: Context, url: String, rawImageDraweeView: SimpleDraweeView) {
            val builder = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setImageDecodeOptions(
                    ImageDecodeOptionsBuilder()
                        .setPreDecodeFrameCount(3)
                        .build()
                )
                .setProgressiveRenderingAnimatedEnabled(true)
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT).also {
                    if (enableIgnoreMemoryCache(context)) {
                        it.disableMemoryCache()
                    }
                    if (enableIgnoreDiskCache(context)) {
                        it.disableDiskCache()
                    }
                }
            val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(builder.build())
                .setOldController(rawImageDraweeView!!.controller).also {
                    it.autoPlayAnimations = enableAnimatedAutoPlay(context)
                }
                .build()
            rawImageDraweeView!!.controller = controller
        }

        /**
         * the code to display the SRed image
         * */
        fun showSrImage(context: Context, url: String, srImageDraweeView: SimpleDraweeView) {
            val builderSuper = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setImageDecodeOptions(
                    ImageDecodeOptionsBuilder()
                        .setPreDecodeFrameCount(3)
                        .build()
                )
                .setProgressiveRenderingAnimatedEnabled(true)
//                .setPostprocessor(SRPostProcessor())            //One line code to open the SR function, redundant in these two parts
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT).also {
                    if (enableIgnoreMemoryCache(context)) {
                        it.disableMemoryCache()
                    }
                    if (enableIgnoreDiskCache(context)) {
                        it.disableDiskCache()
                    }
                }
            val controllerSuper: DraweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(builderSuper.build())
                .setOldController(srImageDraweeView!!.controller).also {
                    it.autoPlayAnimations = enableAnimatedAutoPlay(context)
                }
                .build()
            srImageDraweeView!!.controller = controllerSuper
        }
    }
}