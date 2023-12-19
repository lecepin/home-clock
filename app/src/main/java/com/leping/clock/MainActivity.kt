package com.leping.clock

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var _exitTime = 0L
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val orientationButton: Button = findViewById(R.id.orientationButton)
        val screenOnButton: Button = findViewById(R.id.screenOnButton)
        val showWebViewButton: Button = findViewById(R.id.showWebViewButton)
        webView = findViewById(R.id.webView)

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
}