package com.sorkow.newtest.view_interface

interface OnMethodLoadedInterface {
    fun onDataLoaded(info: String)
    fun onCheckDone(check: Boolean)
    fun onError(error: String)

}