package com.bytedance.fresco.showsample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.support.v7.preference.SwitchPreferenceCompat
import android.view.MenuItem
import android.widget.Toast
import com.bytedance.applog.AppLog
import com.bytedance.fresco.showsample.case.CaseActivity
import com.bytedance.fresco.showsample.utils.FragmentFactory
import com.facebook.drawee.backends.pipeline.Fresco
import org.greenrobot.eventbus.EventBus

private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : BaseActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    val factory: FragmentFactory = FragmentFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fresco_settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, HeaderFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.fresco_title_activity_settings)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
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

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = factory.instantiate(
            classLoader,
            pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat() {

        @SuppressLint("RestrictedApi")
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)

            val settingSuperResolutionMode: ListPreference? = findPreference("setting_select_mode") as ListPreference?
            val settingSuperFileSource: SwitchPreferenceCompat? =
                findPreference(ShowSampleConst.SETTING_ENABLE_USER_URL) as SwitchPreferenceCompat?
            val settingSuperCustomUrl: EditTextPreference? =
                findPreference(ShowSampleConst.SETTING_USER_INPUT_URL) as EditTextPreference?
            var isOpenSuperResolution = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(ShowSampleConst.SETTING_ENABLE_SR, false);

            if (!isOpenSuperResolution) {
                settingSuperResolutionMode?.isVisible = false;
                settingSuperFileSource?.isVisible = false;
                settingSuperCustomUrl?.isVisible = false;
            } else {
                settingSuperResolutionMode?.isVisible = true;
                settingSuperFileSource?.isVisible = true;
                settingSuperCustomUrl?.isVisible = true;
            }
            /**
             * app id
             */
            val appidPreference = findPreference("setting_account") as EditTextPreference
            appidPreference.summary = AppLog.getAid()
            appidPreference.onPreferenceChangeListener =
                object : Preference.OnPreferenceChangeListener {
                    override fun onPreferenceChange(p0: Preference?, p1: Any?): Boolean {
                        val summary = p1 as String
                        p0!!.summary = summary
                        ShowSampleApplication.getAppSharedPreferences()
                            .edit().putString(ShowSampleApplication.AID, summary).apply()
                        return true
                    }
                }

            /**
             * did
             */
            val didPreference = findPreference("setting_account_did")
            didPreference.summary = AppLog.getDid()
            didPreference.setOnPreferenceClickListener {
                toClipBoard(context!!, it.summary.toString())
                true
            }

            /**
             * uuid
             */
            val uuidPreference = findPreference("setting_account_uuid")
            uuidPreference.summary = AppLog.getUserUniqueID()
            uuidPreference.setOnPreferenceClickListener {
                toClipBoard(context!!, it.summary.toString())
                true
            }

            /**
             * setting_mem_cache_clean
             */
            val memCacheCleanPreference: Preference? = findPreference("setting_mem_cache_clean")
            memCacheCleanPreference!!.setOnPreferenceClickListener {
                Fresco.getImagePipeline().clearMemoryCaches();
                Toast.makeText(context, "清理内存完成", Toast.LENGTH_SHORT).show()
                true
            }

            /**
             * setting_cache_clean
             */
            val cacheCleanPreference: Preference? = findPreference("setting_cache_clean")
            cacheCleanPreference!!.setOnPreferenceClickListener {
                Fresco.getImagePipeline().clearCaches()
                Toast.makeText(context, "清理完成", Toast.LENGTH_SHORT).show()
                refreshCacheSize(cacheCleanPreference)
                true
            }
            refreshCacheSize(cacheCleanPreference)

            /**
             * setting_network
             */
            val settingNetworkPreference: ListPreference? =
                findPreference("setting_network") as ListPreference?
            settingNetworkPreference!!.setOnPreferenceChangeListener { _, _ ->
                FrescoFactory.init(ShowSampleApplication.getInstance()!!, AppLog.getAid())
                true
            }

            /**
             * setting_animated_progressive
             */
            val settingAnimatedProgressive: SwitchPreferenceCompat? =
                findPreference("setting_animated_progressive") as SwitchPreferenceCompat?
            settingAnimatedProgressive!!.setOnPreferenceClickListener {
                FrescoFactory.initProgressive(ShowSampleApplication.getInstance()!!)
                true
            }

            /**
             * setting_fail_retry
             */
            val settingFailRetry: SwitchPreferenceCompat? =
                findPreference("setting_fail_retry") as SwitchPreferenceCompat?
            settingFailRetry!!.setOnPreferenceClickListener {
                FrescoFactory.initFailRetry(ShowSampleApplication.getInstance()!!)
                true
            }
            /**
             * setting_case
             */
            val settingCase: Preference? = findPreference("setting_case")
            settingCase!!.setOnPreferenceClickListener {
                startActivity(Intent(activity, CaseActivity::class.java))
                true
            }

            /**
             * setting_open_super_resolution
             * */
            val settingSuperResolution: SwitchPreferenceCompat? =
                findPreference(ShowSampleConst.SETTING_ENABLE_SR) as SwitchPreferenceCompat?
            settingSuperResolution!!.setOnPreferenceClickListener {
                isOpenSuperResolution = PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getBoolean(ShowSampleConst.SETTING_ENABLE_SR, false);

                //Switch status of [启用用户导入文件]
                val isOpenUseUserUrl: Boolean = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(ShowSampleConst.SETTING_ENABLE_USER_URL, false)
                if (isOpenSuperResolution == true) {
                    if (isOpenUseUserUrl) {
                        EventBus.getDefault().post("openUserUrl")
                    } else {
                        EventBus.getDefault().post("openSr")
                    }

                    settingSuperResolutionMode?.isVisible = true;
                    settingSuperFileSource?.isVisible = true;
                    settingSuperCustomUrl?.isVisible = true;
                    true
                } else {
                    EventBus.getDefault().post("closeSr")
                    settingSuperResolutionMode?.isVisible = false;
                    settingSuperFileSource?.isVisible = false;
                    settingSuperCustomUrl?.isVisible = false;
                    false
                }
            }

            //Switch status of [启用用户导入文件]
            val settingUseUserUrl: SwitchPreferenceCompat? =
                findPreference(ShowSampleConst.SETTING_ENABLE_USER_URL) as SwitchPreferenceCompat?
            settingUseUserUrl!!.setOnPreferenceClickListener {
                val isOpenUseUserUrl: Boolean = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(ShowSampleConst.SETTING_ENABLE_USER_URL, false)
                if (isOpenUseUserUrl == false) {
                    EventBus.getDefault().post("closeUserUrl")
                    false
                } else {
                    EventBus.getDefault().post("openUserUrl")
                    true
                }
            }

            val cloudControl: Preference? = findPreference("setting_cloud_config") as Preference?
            cloudControl!!.setOnPreferenceClickListener {
                val intent = Intent(this.activity, CloudControlActivity::class.java)
                startActivity(intent)
                true
            }

            val authorizationPreference: Preference? = findPreference("setting_anth_info") as Preference?
            authorizationPreference!!.setOnPreferenceClickListener {
                val intent = Intent(this.activity, AuthorizationActivity::class.java)
                startActivity(intent)
                true
            }

            /**
             * setting_others
             */
            val settingOthers: Preference? = findPreference("setting_others")
            settingOthers!!.setOnPreferenceClickListener {
                startActivity(Intent(activity, TestActivity::class.java))
                true
            }

            /**
             * setting_others1
             */
            val settingOthers1: Preference? = findPreference("setting_others1")
            settingOthers1!!.setOnPreferenceClickListener {
                startActivity(Intent(activity, Test1Activity::class.java))
                true
            }
        }

        private fun refreshCacheSize(cacheCleanPreference: Preference) {
            val diskSizeByte =
                Fresco.getImagePipelineFactory().mainFileCache.size + Fresco.getImagePipelineFactory().smallImageFileCache.size
            val memorySizeByte =
                Fresco.getImagePipelineFactory().bitmapCountingMemoryCache.sizeInBytes + Fresco.getImagePipelineFactory().encodedCountingMemoryCache.sizeInBytes
            val diskSizeMB = diskSizeByte / 1024 / 1024
            val memorySizeMB = memorySizeByte / 1024 / 1024
            cacheCleanPreference.summary = "磁盘缓存：$diskSizeMB Mb, 内存缓存$memorySizeMB Mb"
        }
    }

    class CacheControlFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.cache_preferences, rootKey)
        }

    }
}

fun goSetting(activity: Activity) {
    activity.startActivity(Intent(activity, SettingsActivity::class.java))
}

fun isTTNet(context: Context): Boolean {
    return "TTNet" == PreferenceManager
        .getDefaultSharedPreferences(context).getString("setting_network", "TTNet")
}

fun enableAnimatedProgressive(context: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("setting_animated_progressive", false)
}

fun enableFailRetry(context: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("setting_fail_retry", false)
}

fun enableAnimatedAutoPlay(context: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("setting_animated_auto_play", true)
}

fun enableIgnoreMemoryCache(context: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("setting_ignore_cache", false)
}

fun enableIgnoreDiskCache(context: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean("setting_ignore_disk", false)
}

fun toClipBoard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Label", text)
    cm.setPrimaryClip(clipData)
    Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
}

