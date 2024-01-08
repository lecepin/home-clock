package com.leping.clock

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leping.clock.Utils.TimeUtils.isCurrentTimeInRange
import com.leping.clock.Utils.TimeUtils.validateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MA"
        const val REQUEST_CODE_CA_ACTIVITY = 100
    }

    private var _exitTime = 0L
    private lateinit var webView: WebView
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var enableCheckBox: CheckBox

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val orientationButton: Button = findViewById(R.id.orientationButton)
        val screenOnButton: Button = findViewById(R.id.screenOnButton)
        val showWebViewButton: Button = findViewById(R.id.showWebViewButton)

        webView = findViewById(R.id.webView)
        startTimeEditText = findViewById(R.id.startTimeEditText)
        endTimeEditText = findViewById(R.id.endTimeEditText)
        enableCheckBox = findViewById(R.id.enableCheckbox)

        orientationButton.setOnClickListener {
            requestedOrientation =
                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Toast.makeText(this, "切换到水平方向", Toast.LENGTH_SHORT).show()

        }
        screenOnButton.setOnClickListener {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            Toast.makeText(this, "屏幕已保持常亮", Toast.LENGTH_SHORT).show()

        }
        showWebViewButton.setOnClickListener {
            webView.apply {
                visibility = View.VISIBLE
                settings.apply {
                    domStorageEnabled = true
                    javaScriptEnabled = true
                    blockNetworkImage = false
                }
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                loadUrl("file:///android_asset/index.html")
            }

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        enableCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val startTime = startTimeEditText.text.toString()
                val endTime = endTimeEditText.text.toString()
                if (validateTime(startTime) && validateTime(endTime)) {
                    startMonitoringTimeRange(startTime, endTime)
                } else {
                    enableCheckBox.isChecked = false
                }
            } else {
                job?.cancel()
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else if (webView.visibility == View.VISIBLE) {
            webView.visibility = View.GONE
        } else if (System.currentTimeMillis() - _exitTime > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            _exitTime = System.currentTimeMillis()
        } else {
            finish();
        }
    }

    private fun startMonitoringTimeRange(startTime: String, endTime: String) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                Log.d(TAG, "job$startTime,$endTime,${isCurrentTimeInRange(startTime, endTime)}")
                if (isCurrentTimeInRange(startTime, endTime)) {
                    val intent = Intent(this@MainActivity, CoolDownActivity::class.java)
                    intent.putExtra("startTime", startTime)
                    intent.putExtra("endTime", endTime)
                    intent.putExtra("orientation", requestedOrientation)
                    startActivityForResult(intent, REQUEST_CODE_CA_ACTIVITY)
                }
                delay(2000) // Check every minute
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CA_ACTIVITY && resultCode == CoolDownActivity.RESULT_CODE_BACK_PRESSED) {
            enableCheckBox.isChecked = false
            job?.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        if (enableCheckBox.isChecked) {
            val startTime = startTimeEditText.text.toString()
            val endTime = endTimeEditText.text.toString()
            startMonitoringTimeRange(startTime, endTime)
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }
}