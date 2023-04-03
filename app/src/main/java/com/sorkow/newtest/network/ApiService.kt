package com.sorkow.newtest.network


import com.sorkow.newtest.network.response.GeoResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @GET(Constants.GET_METHOD)
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZXMiOlt7ImlkIjoyfV0sImlhdCI6MTY3MjE4NDQ0NH0.ONCAZr_r8Cdu1cePZz4FRP75ytLrDGtul2qzgkoqnCc")
    fun getMethod(@Query("geo") geo: String?): Observable<GeoResponse>

    @GET(Constants.CHECK_METHOD)
    fun checkMain(): Observable<ResponseBody>
}