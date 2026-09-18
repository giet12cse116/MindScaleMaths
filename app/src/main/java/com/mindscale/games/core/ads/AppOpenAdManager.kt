package com.mindscale.games.core.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.mindscale.games.BuildConfig
import java.util.Date

class AppOpenAdManager(
    private val application: Application
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var isColdStart = true

    // Production vs Test Ad Unit ID (Google Test Ad Unit ID for App Open Ad)
    private val adUnitId = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/9257395921"
    } else {
        "ca-app-pub-5520583411219682/7587387499"
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        fetchAd()
    }

    fun fetchAd() {
        if (isAdAvailable() || isLoadingAd) return
        isLoadingAd = true
        Log.d("AppOpenAdManager", "Fetching App Open Ad (Unit ID: $adUnitId)...")
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d("AppOpenAdManager", "App Open Ad loaded successfully.")
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(
                        "AppOpenAdManager",
                        "App Open Ad failed to load: ${loadAdError.message} (code: ${loadAdError.code})"
                    )
                    isLoadingAd = false
                    appOpenAd = null
                }
            }
        )
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && (Date().time - loadTime < 4 * 3600 * 1000)
    }

    fun showAdIfAvailable(activity: Activity, onAdDismissed: () -> Unit = {}) {
        if (!isShowingAd && isAdAvailable()) {
            Log.d("AppOpenAdManager", "Showing App Open Ad...")
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AppOpenAdManager", "App Open Ad dismissed.")
                    appOpenAd = null
                    isShowingAd = false
                    onAdDismissed()
                    fetchAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AppOpenAdManager", "App Open Ad failed to show: ${adError.message}")
                    appOpenAd = null
                    isShowingAd = false
                    onAdDismissed()
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AppOpenAdManager", "App Open Ad displayed.")
                    isShowingAd = true
                }
            }
            appOpenAd?.show(activity)
        } else {
            Log.d(
                "AppOpenAdManager",
                "Cannot show ad. isShowingAd=$isShowingAd, isAdAvailable=${isAdAvailable()}"
            )
            onAdDismissed()
            fetchAd()
        }
    }

    fun showAdIfAvailableOrWait(
        activity: Activity,
        maxWaitMs: Long = 2500L,
        onAdDismissed: () -> Unit
    ) {
        if (isAdAvailable()) {
            showAdIfAvailable(activity, onAdDismissed)
            return
        }

        var hasFinished = false
        val handler = android.os.Handler(android.os.Looper.getMainLooper())

        val timeoutRunnable = Runnable {
            if (!hasFinished) {
                hasFinished = true
                Log.d("AppOpenAdManager", "App open ad splash wait timed out after ${maxWaitMs}ms")
                onAdDismissed()
            }
        }

        handler.postDelayed(timeoutRunnable, maxWaitMs)

        val pollRunnable = object : Runnable {
            override fun run() {
                if (hasFinished) return
                if (isAdAvailable()) {
                    hasFinished = true
                    handler.removeCallbacks(timeoutRunnable)
                    showAdIfAvailable(activity, onAdDismissed)
                } else if (!isLoadingAd) {
                    hasFinished = true
                    handler.removeCallbacks(timeoutRunnable)
                    onAdDismissed()
                } else {
                    handler.postDelayed(this, 100L)
                }
            }
        }
        handler.postDelayed(pollRunnable, 100L)
    }

    override fun onStart(owner: LifecycleOwner) {
        currentActivity?.let { activity ->
            if (isAdAvailable()) {
                showAdIfAvailable(activity)
            } else {
                fetchAd()
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
    }
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
