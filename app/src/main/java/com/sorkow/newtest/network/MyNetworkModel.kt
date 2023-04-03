package com.sorkow.newtest.network

import android.util.Log
import com.sorkow.newtest.network.response.GeoResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

class MyNetworkModel: NetworkBaseModel() {

    public fun getMethodInfo(geo: String?) : Observable<GeoResponse> {
        return netService.getMethod(geo)
            .compose(applySchedulers())
    }

    public fun makeCheck() : Observable<ResponseBody> {
        return checkService.checkMain()
            .compose(applySchedulers())
    }
}