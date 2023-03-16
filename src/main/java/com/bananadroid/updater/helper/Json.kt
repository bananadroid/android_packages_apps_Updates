/*
 * Copyright (C) 2023 BananaDroid
 * Copyright (C) 2023 Andrea Canale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bananadroid.updater.helper

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import com.bananadroid.updater.Model.Update
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL

class Json {

    private fun GET(device: String?): String? {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val url = URL("https://raw.githubusercontent.com/bananadroid-devices/bananadroid_devices/main/${device}.json")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            inputStream.bufferedReader().use {
                return it.readLine()
            }
        }
    }

    fun get_device_json(device: String?): Update? {
        if(device == null)
            return null
        return Gson().fromJson<Update>(GET(device), Update::class.java)
    }
}