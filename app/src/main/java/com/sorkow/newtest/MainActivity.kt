package com.sorkow.newtest

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.sorkow.newtest.presenter.MyPresenter
import com.sorkow.newtest.utils.StringHelper
import com.sorkow.newtest.view_interface.OnMethodLoadedInterface
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeoutException


class MainActivity : AppCompatActivity(), OnMethodLoadedInterface {
    lateinit var conversion: Map<String, Any>
    var aid: String? = null
    var gauid: String? = null
    var appsflyerUID: String? = null
    var campaign: String = ""
    lateinit var jsonBody: JSONObject

    lateinit var presenter: MyPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if(AppLoadApp.getAppInstance().check(this@MainActivity)) {
            presenter = MyPresenter(this)
            Thread{ getGAID() }.start()
            regAppsFlyer()

        } else {
            //show game
        }
    }

    private fun regAppsFlyer() {
        appsflyerUID = AppsFlyerLib.getInstance().getAppsFlyerUID(this)!!
        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                try {
                    conversion = conversionData
                    parseAppsflyer(conversionData)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, conversionData.toString(), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.i("tta", "qwe")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                Log.i("tta", "qwe")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, attributionData.toString(), Toast.LENGTH_LONG).show()
                }
            }
            override fun onAttributionFailure(errorMessage: String) {
                Log.i("tta", "qwe")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
        AppsFlyerLib.getInstance().registerConversionListener(this, conversionListener)
        AppsFlyerLib.getInstance().start(this)
    }

    private fun parseAppsflyer(conversionData: Map<String, Any>) {
//        val deviceId: String = conversionData["advertising_id"] as String
//        val media_source: String = conversionData["media_source"] as String
//        val campaign_id: String = conversionData["campaign_id"] as String
//        val queue = Volley.newRequestQueue(this)

        jsonBody = JSONObject()
        val str = getDeviceName()
        val af_status: String = conversionData["af_status"] as String
        if(!af_status.equals("Organic")) {
            campaign = conversionData["campaign"] as String
            jsonBody.put("af_status", conversionData["af_status"])
            jsonBody.put("af_channel", conversionData["media_source"])
            jsonBody.put("ad_id", conversionData["advertising_id"])
            jsonBody.put("campaign_id", conversionData["campaign_id"])
            jsonBody.put("campaign_group_name", campaign)
            jsonBody.put("campaign_group_id", conversionData["campaign_id"])
            jsonBody.put("adgroup_name", conversionData["adgroup"])
            jsonBody.put("adgroup_id", conversionData["adgroup_id"])
            jsonBody.put("campaign_name", campaign)

            jsonBody.put("geotag", campaign)
            jsonBody.put("buyer_name", "WW")
            jsonBody.put("creative_name", "Test2")
            jsonBody.put("app_id", gauid)
            jsonBody.put("device_id", aid)
            jsonBody.put("reserved_1", getDeviceName())
            jsonBody.put("reserved_2", "Value 2")
            jsonBody.put("reserved_3", "Value 3")
            jsonBody.put("offer_link", "https://example.com/offer")
            jsonBody.put("package_name", BuildConfig.APPLICATION_ID)
        } else {
            jsonBody.put("af_status", conversionData["af_status"])
            jsonBody.put("af_channel", "")
            jsonBody.put("ad_id", "")
            jsonBody.put("campaign_id", "")
            jsonBody.put("campaign_group_name", "")
            jsonBody.put("campaign_group_id", "")
            jsonBody.put("adgroup_name", "")
            jsonBody.put("adgroup_id", "")
            jsonBody.put("campaign_name", "")

            jsonBody.put("geotag", "")
            jsonBody.put("buyer_name", "")
            jsonBody.put("creative_name", "Test2")
            jsonBody.put("app_id", gauid)
            jsonBody.put("device_id", aid)
            jsonBody.put("reserved_1", getDeviceName())
            jsonBody.put("reserved_2", "")
            jsonBody.put("reserved_3", "")
            jsonBody.put("offer_link", "https://example.com/offer")
            jsonBody.put("package_name", BuildConfig.APPLICATION_ID)
        }
//        checkMainInfo()
        presenter.makeCheck()
    }

    private fun getGeoMethodInfo() {
        presenter.getMethodInfo(campaign)
    }

    private fun getGAID() {
        try {
            var adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
            gauid = adInfo.id.toString()
        } catch (e: IOException) {
            gauid = null
        } catch (e: GooglePlayServicesNotAvailableException) {
            gauid = null
        } catch (e: TimeoutException) {
            gauid = null
        }
    }

    override fun onDataLoaded(info: String) {

    }

    override fun onCheckDone(check: Boolean) {
        if(check) {
            getGeoMethodInfo()
        } else {
            //show game
        }
    }

    override fun onError(error: String) {
        //show game
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault()).startsWith(manufacturer.lowercase(Locale.getDefault()))) {
            StringHelper.capitalize(model)
        } else {
            StringHelper.capitalize("$manufacturer $model")
        }
    }

    private fun checkMainInfo() {
        val queue = Volley.newRequestQueue(this)
        val data: ByteArray = Base64.decode("aHR0cHM6Ly9yZWFsYXZpLnNwYWNlL2drZHNiNg==", Base64.DEFAULT)
        val text = String(data, StandardCharsets.UTF_8)
        val stringRequest = StringRequest(Request.Method.GET, text, { response ->
            Log.i("Volley", "Response is: $response")
            runOnUiThread {
                Toast.makeText(this@MainActivity, "200 good", Toast.LENGTH_LONG).show()
                getGeoMethodInfo()
            }
        }, { e ->
            run {
                if (e.networkResponse.statusCode == 403) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, " 403 adasdasd", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_LONG).show()
                }
                Log.i("Volley", "Response is: $e")
            }
        })
        queue.add(stringRequest)
    }
}