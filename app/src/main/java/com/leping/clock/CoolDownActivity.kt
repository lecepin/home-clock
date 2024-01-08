package com.leping.clock

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.leping.clock.Utils.TimeUtils.isCurrentTimeInRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CoolDownActivity : AppCompatActivity() {
    companion object {
        const val RESULT_CODE_BACK_PRESSED = 1
    }

    private var monitoringJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cool_down)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        requestedOrientation =
            intent.getIntExtra("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)

        monitoringJob = CoroutineScope(Dispatchers.Main).launch {
            val startTime = intent.getStringExtra("startTime") ?: return@launch
            val endTime = intent.getStringExtra("endTime") ?: return@launch

            while (isActive) {
                if (!isCurrentTimeInRange(startTime, endTime)) {
                    finish()
                }
                delay(2000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        monitoringJob?.cancel()
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE_BACK_PRESSED)
        super.onBackPressed()
    }
}