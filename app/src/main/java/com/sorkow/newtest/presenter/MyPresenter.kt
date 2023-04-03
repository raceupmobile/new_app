package com.sorkow.newtest.presenter

import android.util.Log
import com.sorkow.newtest.network.MyNetworkModel
import com.sorkow.newtest.view_interface.OnMethodLoadedInterface
import retrofit2.HttpException

class MyPresenter(view: OnMethodLoadedInterface) {
    val model = MyNetworkModel()
    var view: OnMethodLoadedInterface
    init {
        this.view = view
    }

    public fun getMethodInfo(geo: String?) {
        model.getMethodInfo(geo).subscribe({ response ->
            run {
                view.onDataLoaded(response.report.domain)
                Log.d("Response", "Response message: ${response}")
            }
        }, { error ->
            run {
                view.onError("Error: ${error.message}")
                Log.e("Error", "Error: ${error.message}")
            }
        })
    }

    public fun makeCheck() {
        model.makeCheck().subscribe({ response ->
            run {
                view.onCheckDone(true)
                Log.d("Response", "Response message: ${response}")
            }
        }, { error ->
            run {
                val httpException: Int = (error as HttpException).code()
                if(httpException == 403) view.onCheckDone(false)
                else view.onError("Error: ${error.message}")
                Log.e("Error", "Error: ${error.message}")
            }
        })
    }
}