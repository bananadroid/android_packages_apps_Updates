package com.bananadroid.updater

import android.R.attr.src
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bananadroid.updater.helper.Downloader
import com.bananadroid.updater.helper.Json
import com.bananadroid.updater.helper.SystemProperties
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    private val system_properties = SystemProperties()
    private val downloader = Downloader()
    private val json_fetcher = Json()

    private fun download_image(url: String): Bitmap? {
        try {
            Log.e("src", url)
            val url = URL(url)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val myBitmap = BitmapFactory.decodeStream(input)
            Log.e("Bitmap", "returned")
            return myBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    private fun fill_page(device_name: String, update_btn: Button, current_version: String?): Boolean {
        val release = json_fetcher.get_device_json(device_name)
        val progressbar = findViewById<ProgressBar>(R.id.download_progress)
        update_btn.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            Toast.makeText(applicationContext, "Download started ", Toast.LENGTH_SHORT).show()
            downloader.progress_bar = progressbar
            if (release != null) {
                downloader.downloadFile(release.version + ".zip", "BananaDroid OTA", release.download_url, applicationContext, applicationContext as Activity)
            }
        }

        if (release != null) {
            return release.version != current_version && current_version != ""
        }else {
            return false
        }
    }

   private fun gen_image(imageView: ImageView, device: String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        imageView.setImageBitmap(download_image("https://github.com/bananadroid-devices/bananadroid_devices/raw/main/photos/${device}.png"))
   }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textbox = findViewById<TextView>(R.id.info)
        val device_image = findViewById<ImageView>(R.id.device)
        val device_name = system_properties.getSystemProperty("ro.banana.device")
        val update_string = findViewById<TextView>(R.id.update_notification)
        val check_for_update_btn = findViewById<TextView>(R.id.checks_btn)
        val update_btn = findViewById<Button>(R.id.update_btn)
        val changelog_btn = findViewById<Button>(R.id.device_changelog_btn)
        val build_date = "Build date: " + system_properties.getSystemProperty("ro.banana.display.date")
        val phone = android.os.Build.MODEL + " - " + android.os.Build.PRODUCT
        val patch = "Security patch: " + android.os.Build.VERSION.SECURITY_PATCH

        if(device_name == null || device_name == ""){
            val grid = findViewById<LinearLayout>(R.id.header)
            grid.removeViewAt(1)
            textbox.text = "DEVICE NOT SUPPORTED";
            textbox.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textbox.translationX = 0.0F
        }else{
            val current_version = system_properties.getSystemProperty("ro.banana.version");
            textbox.text = "${phone}\n${patch}\n${build_date}"
            gen_image(device_image, device_name)
            changelog_btn.visibility = View.VISIBLE
            check_for_update_btn.visibility = View.VISIBLE

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val available = fill_page(device_name, update_btn, current_version)
                    if(available){
                        update_string.visibility = View.VISIBLE
                        update_btn.visibility = View.VISIBLE

                    }
                    check_for_update_btn.setOnClickListener {
                        GlobalScope.launch {
                            withContext(Dispatchers.Main) {
                                val available = fill_page(device_name, update_btn, current_version)
                                if(available){
                                    update_string.visibility = View.VISIBLE
                                    update_btn.visibility = View.VISIBLE

                                }
                            }
                        }
                    }
                }
            }
        }
    }

}