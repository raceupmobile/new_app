package com.sorkow.newtest

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.appsflyer.AppsFlyerLib

class AppLoadApp: Application() {
    private val AF_DEV_KEY_MY = "nsF6YZsWz5sKAYVTEbPTXG"

    companion object {
        @JvmField var appInstance: AppLoadApp? = null
        @JvmStatic fun getAppInstance(): AppLoadApp {
            return appInstance as AppLoadApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        AppsFlyerLib.getInstance().init(AF_DEV_KEY_MY, null, this)
    }

    fun getContext(): Context? {
        return appInstance?.applicationContext
    }

    fun check(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if(wifiInfo!=null && wifiInfo.isConnected) return true
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if(wifiInfo!=null && wifiInfo.isConnected()) return true
        wifiInfo = cm.getActiveNetworkInfo();
        if(wifiInfo!=null && wifiInfo.isConnected()) return true
        return false
    }
}