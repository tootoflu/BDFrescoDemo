package com.bytedance.fresco.showsample

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bytedance.fresco.showsample.FigureDetailActivity.Companion.goDetail
import com.bytedance.fresco.showsample.SrDetailActivity.Companion.goSrDetail
import com.bytedance.fresco.showsample.model.ImageUrlModel
//import com.bytedance.fresco.sr.SRPostProcessor
import com.bytedance.fresco.showsample.model.ImageUrlModel.Companion.HEIC_PROGRESSVIE_STATIC
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode.MAIN
import org.json.JSONObject


class MainActivity : BaseActivity() {

    private var mPagerAdapter: MainPagerAdapter? = null
    private var imageLogList: ShowSampleApplication.ImageLogListener? = null
    private var logTextView: TextView? = null
    private var mainTabLayout: TabLayout? = null
    private var mainViewPager: ViewPager? = null

    @SuppressLint("CI_ByteDanceKotlinRules_Not_Allow_findViewById_Invoked_In_UI")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fresco_activity_main)
        logTextView = findViewById(R.id.logTextView)
        mainTabLayout = findViewById(R.id.mainTabLayout)
        mainViewPager = findViewById(R.id.mainViewPager)
        val mainLogBtn = findViewById<ImageView>(R.id.mainLogBtn)
        val mainSettingBtn = findViewById<ImageView>(R.id.mainSettingBtn)
        logTextView!!.movementMethod = ScrollingMovementMethod.getInstance()

        EventBus.getDefault().register(this)

        mainLogBtn.setOnClickListener {
            logTextView!!.visibility = if (logTextView!!.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        mainSettingBtn.setOnClickListener {
            goSetting(this)
        }

        val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
        initImageLog(model)

        initSrPage()
        /**
         * Observe the change of data in ViewModel(ImageUrlModel),when something in
         * MutableList<ImageUrlModel.ImagePageData> changes, it will callback and the method setupMainView() will
         * update the main page according to the change
         * */
        model.imageData.observe(this, Observer<MutableList<ImageUrlModel.ImagePageData>> {
            setupMainView(it!!)
        })
        ShowSampleApplication.addImageLogListener(imageLogList!!)
    }

    /**
     * Initiate SR page according to the status of two SwitchPreferenceCompats:
     * ShowSampleConst.SETTING_ENABLE_SR and ShowSampleConst.SETTING_ENABLE_USER_URL
     * */
    private fun initSrPage() {
        val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
        val isOpenSr =
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ShowSampleConst.SETTING_ENABLE_SR, false)
        // If the user open the SwitchPreferenceCompat of [启用用户导入文件]
        val isOpenUserUrlMode = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(ShowSampleConst.SETTING_ENABLE_USER_URL, false)
        if (isOpenSr) {
            if (isOpenUserUrlMode) {
                val inputUrl = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(ShowSampleConst.SETTING_USER_INPUT_URL, "")
                if (inputUrl != null) {
                    if (inputUrl.startsWith("http://") || inputUrl.startsWith("https://")) {
                        model.useUserUrl(inputUrl!!)
                    }
                }
            } else {
                model.addSuperPage()
            }
        }
    }

    //Subscribe the Events sent by EventBus, and do reaction according to them
    @Subscribe(threadMode = MAIN)
    fun onGetSuperResolutionStatus(eventMessage: String) {
        if (eventMessage == "closeSr") {
            val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
            model.removeSuperPage()
        }
        if (eventMessage == "openSr") {
            val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
            // If the user open the SwitchPreferenceCompat of [启用用户导入文件]
            val isOpenUserUrlMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(ShowSampleConst.SETTING_ENABLE_USER_URL, false)

            if (isOpenUserUrlMode == false) {
                model.addSuperPage()
            } else {
                val inputUrl = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(ShowSampleConst.SETTING_USER_INPUT_URL, "")
                model.useUserUrl(inputUrl!!)
            }
        }

        if (eventMessage == "openUserUrl") {
            val inputUrl = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(ShowSampleConst.SETTING_USER_INPUT_URL, "")
            val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
            model.useUserUrl(inputUrl!!)
        }
        if (eventMessage == "closeUserUrl") {
            val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
            val isOpenSr = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(ShowSampleConst.SETTING_ENABLE_SR, false)
            if (isOpenSr != false) {
                model.disposeUserUrl()
            }
        }

        if (eventMessage == "ResponseArrive") {
            val model = ViewModelProviders.of(this)[ImageUrlModel::class.java]
            model.useUrl()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        ShowSampleApplication.removeImageLogListener(imageLogList!!)
    }

    private fun initImageLog(model: ImageUrlModel) {
        imageLogList = object :
            ShowSampleApplication.ImageLogListener {
            override fun onImageLoad(
                isSucceed: Boolean,
                requestId: String?,
                jsonObject: JSONObject?
            ) {
                runOnUiThread {
                    if (jsonObject != null) {
                        model.logData.value?.set(jsonObject.optString("uri"), jsonObject)
                    }
                    logTextView!!.append(
                        System.lineSeparator()
                                + "=================START==================" + System.lineSeparator()
                    )
                    logTextView!!.append(jsonObject!!.toString(4))
                    logTextView!!.append(
                        System.lineSeparator()
                                + "==================END===================" + System.lineSeparator()
                    )
                    val offset = logTextView!!.lineCount * logTextView!!.lineHeight
                    if (offset > logTextView!!.height) {
                        logTextView!!.scrollTo(
                            0,
                            offset - logTextView!!.height + logTextView!!.lineHeight * 2
                        )
                    }
                }
            }
        }
    }

    private fun setupMainView(imagePageDatas: MutableList<ImageUrlModel.ImagePageData>) {
        val fragments = ArrayList<Fragment>()
        val isSuperOpen =
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ShowSampleConst.SETTING_ENABLE_SR, true)

        imagePageDatas.forEach {
        if (it.pageName != ShowSampleConst.SR_PACKAGE_NAME || isSuperOpen) {
            val bundle = Bundle()
            bundle.putStringArrayList("url", ArrayList(it.imageUrls))
            val fragment = MainFragment()
            fragment.setPageName(it.pageName)
            fragment.setPackageName(it.pageName)
            fragment.arguments = bundle
            fragments.add(fragment)
            mainTabLayout!!.addTab(mainTabLayout!!.newTab().setText(it.pageName))
        }
        }

        mainTabLayout!!.setupWithViewPager(mainViewPager, false)
        mPagerAdapter = MainPagerAdapter(fragments, imagePageDatas, supportFragmentManager)
        mainViewPager!!.adapter = mPagerAdapter
    }
}

@SuppressLint("WrongConstant")
class MainPagerAdapter(
    private val fragments: List<Fragment>,
    private val imagePageData: List<ImageUrlModel.ImagePageData>,
    private val fragmentManager: FragmentManager
) :
    FragmentPagerAdapter(fragmentManager) {

    var mCurTransaction: FragmentTransaction? = null
    var mCurrentPrimaryItem: Fragment? = null

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (position >= imagePageData.size) {
            return null
        }
        return imagePageData[position].pageName
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        val fragment = `object` as Fragment
        if (fragment !== this.mCurrentPrimaryItem) {
            this.mCurrentPrimaryItem = fragment
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = fragmentManager.beginTransaction()
        }

        val itemId = getItemId(position)
        val name = makeFragmentName(container.id, itemId)
        var fragment = fragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            this.mCurTransaction!!.attach(fragment)
        } else {
            fragment = getItem(position)
            this.mCurTransaction!!.add(container.id, fragment, makeFragmentName(container.id, itemId))
        }

        if (fragment !== mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false)
            fragment.userVisibleHint = false
        }

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = fragmentManager.beginTransaction()
        }

        this.mCurTransaction!!.remove((`object` as Fragment))
    }

    override fun finishUpdate(container: ViewGroup) {
        if (this.mCurTransaction != null) {
            this.mCurTransaction!!.commitNowAllowingStateLoss()
            this.mCurTransaction = null
        }
    }

    private fun makeFragmentName(viewId: Int, id: Long): String? {
        return "android:switcher:$viewId:$id"
    }
}

class MainFragment() : Fragment() {

    private var packageName: String = ""
    private var pageName: String = ""

    @SuppressLint("CI_ByteDanceKotlinRules_Not_Allow_findViewById_Invoked_In_UI")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val urls = arguments!!.getStringArrayList("url")
        val mainRecyclerView = view.findViewById<RecyclerView>(R.id.mainRecyclerView);
        with(mainRecyclerView) {
            var spanCount = 3
            if (pageName == HEIC_PROGRESSVIE_STATIC) {
                spanCount = 1
            }
            layoutManager = GridLayoutManager(activity, spanCount)
            adapter = MainContentAdapter(pageName, urls!!, layoutManager as GridLayoutManager, activity!!, packageName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fresco_layout_main_fragment, container, false);
    }

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    fun setPageName(pageName: String) {
        this.pageName = pageName
    }
}

/**
 * Main RecyclerView Adapter
 */
class MainContentAdapter(
    private val pageName: String,
    private val urls: ArrayList<String>,
    private val layoutManager: GridLayoutManager,
    private val context: FragmentActivity,
    private val packageName: String
) : RecyclerView.Adapter<MainContentHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainContentHolder {
        return MainContentHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fresco_layout_main_item, parent, false)
        ).also {
            it.itemView.setOnClickListener(this)
        }
    }

    override fun getItemCount(): Int {
        return urls.size
    }

    override fun onBindViewHolder(holder: MainContentHolder, position: Int) {
        val context = holder.itemView.context
        holder.itemView.layoutParams.height = layoutManager.width / layoutManager.spanCount
        val width = layoutManager.width / layoutManager.spanCount
        val height = width
        //if the package is SR, do the Sr process before displaying the image
        //The code here is redundant，the only difference is the one line to open SR, may need to be improved in the future
        if (packageName == ShowSampleConst.SR_PACKAGE_NAME) {

            val builder = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(urls[position]))
                .setImageDecodeOptions(
                    ImageDecodeOptionsBuilder()
                        .setPreDecodeFrameCount(3)
                        .build()
                )
                .also {
                    if (pageName == "heic") {
                        it.isMultiplexerEnabled = false
                    }
                }
//                .setResizeOptions(ResizeOptions(width, height))
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
                .setOldController(holder.draweeView!!.controller)
                .setAutoPlayAnimations(enableAnimatedAutoPlay(context))
                .build()
            holder.draweeView!!.controller = controller
        } else {
            val builder = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(urls[position]))
                .setImageDecodeOptions(
                    ImageDecodeOptionsBuilder()
                        .setPreDecodeFrameCount(3)
                        .build()
                )
                .also {
                    if (pageName == "heic") {
                        it.isMultiplexerEnabled = false
                    }
                }
                .setResizeOptions(ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT).also {
                    if (enableIgnoreMemoryCache(context)) {
                        it.disableMemoryCache()
                    }
                    if (enableIgnoreDiskCache(context)) {
                        it.disableDiskCache()
                    }
                }
                .also {
                    if (pageName == "静图") {
                        it.cacheChoice = ImageRequest.CacheChoice.DEFAULT
                    } else if (pageName == ("GIF动图")) {
                        it.cacheChoice = ImageRequest.CacheChoice.SMALL
                    } else if (pageName == "awebp" || pageName == "heic") {
                        it.cacheChoice = ImageRequest.CacheChoice.CUSTOM
                        if(pageName == "awebp") {
                            it.customCacheName = "im_fresco_cache"
                        } else {
                            it.customCacheName = "live_fresco_cache"
                        }
                    }
                }
            val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(builder.build())
                .setOldController(holder.draweeView!!.controller)
                .setAutoPlayAnimations(enableAnimatedAutoPlay(context))
                .build()
            holder.draweeView!!.controller = controller
        }
    }

    override fun onClick(v: View?) {
        val positon = layoutManager.getPosition(v!!)
        if (packageName == ShowSampleConst.SR_PACKAGE_NAME) {
            goSrDetail(context, urls[positon])
        } else {
            goDetail(context, urls[positon])
        }
    }

}

/**
 * RecyclerView Holder
 */
class MainContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val draweeView: SimpleDraweeView? by lazy {
        itemView.findViewById<SimpleDraweeView?>(R.id.draweeView)
    }
}
