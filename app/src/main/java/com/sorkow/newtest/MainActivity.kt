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
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var conversion: Map<String, Any>
    var aid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if(AppLoadApp.getAppInstance().check(this@MainActivity)) {
            checkMainInfo()
            initAppsFlyer()
        } else {
            //show game
        }
    }

    private fun initAppsFlyer() {
        val ttt: String = AppsFlyerLib.getInstance().getAppsFlyerUID(this)!!
        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                try {
                    conversion = conversionData
                    dealIt(conversionData)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, conversionData.toString(), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) { e.printStackTrace() }
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

    private fun dealIt(conversionData: Map<String, Any>) {
//        val deviceId: String = conversionData["advertising_id"] as String
//        val campaignName: String = conversionData["campaign"] as String
//        val media_source: String = conversionData["media_source"] as String
//        val campaign_id: String = conversionData["campaign_id"] as String

        val queue = Volley.newRequestQueue(this)
        val jsonBody = JSONObject()
        val str = getDeviceName()
        val af_status: String = conversionData["af_status"] as String
        if(!af_status.equals("Organic")) {
            jsonBody.put("geotag", "COLA14")
            jsonBody.put("buyer_name", "WW")
            jsonBody.put("creative_name", "Test2")
            jsonBody.put("af_status", conversionData["af_status"])
            jsonBody.put("af_channel", "Facebook")
            jsonBody.put("ad_id", conversionData["advertising_id"])
            jsonBody.put("campaign_id", conversionData["campaign_id"])
            jsonBody.put("campaign_group_name", "Summer Campaign")
            jsonBody.put("campaign_group_id", "123")
            jsonBody.put("adgroup_name", "Ad Group 1")
            jsonBody.put("adgroup_id", "456")
            jsonBody.put("campaign_name", conversionData["campaign"])
            jsonBody.put("app_id", BuildConfig.APPLICATION_ID)
            jsonBody.put("device_id", aid)
            jsonBody.put("reserved_1", getDeviceName())
            jsonBody.put("reserved_2", "Value 2")
            jsonBody.put("reserved_3", "Value 3")
            jsonBody.put("offer_link", "https://example.com/offer")
            jsonBody.put("package_name", BuildConfig.APPLICATION_ID)
        } else {
            jsonBody.put("geotag", af_status)
            jsonBody.put("app_id", BuildConfig.APPLICATION_ID)
            jsonBody.put("device_id", aid)
            jsonBody.put("reserved_1", getDeviceName())
            jsonBody.put("offer_link", "https://example.com/offer")
            jsonBody.put("package_name", BuildConfig.APPLICATION_ID)
        }

        var url = "http://185.46.9.229:4002/v1/app"
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Log.i("Volley", "Response is: $response")
            }, { error ->
                Log.i("Volley", "Response is: $error")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZXMiOlt7ImlkIjoyfV0sImlhdCI6MTY3MjE4NDQ0NH0.ONCAZr_r8Cdu1cePZz4FRP75ytLrDGtul2qzgkoqnCc"
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun checkMainInfo() {
        val queue = Volley.newRequestQueue(this)
        val data: ByteArray = Base64.decode("aHR0cHM6Ly9yZWFsYXZpLnNwYWNlL2drZHNiNg==", Base64.DEFAULT)
        val text = String(data, StandardCharsets.UTF_8)
//        var url = "https://realavi.space/gkdsb6"
        val stringRequest = StringRequest(Request.Method.GET, text, { response ->
                // Display the first 500 characters of the response string.
                Log.i("Volley", "Response is: $response")
            runOnUiThread {
                Toast.makeText(this@MainActivity, "200 good", Toast.LENGTH_LONG).show()
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


    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault())
                .startsWith(manufacturer.lowercase(Locale.getDefault()))
        ) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String?): String? {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }
}