package com.mindscale.games

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.mindscale.games.core.ads.AppOpenAdManager

class MindScaleApplication : Application() {

    lateinit var appOpenAdManager: AppOpenAdManager
        private set

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        appOpenAdManager = AppOpenAdManager(this)
    }
}
