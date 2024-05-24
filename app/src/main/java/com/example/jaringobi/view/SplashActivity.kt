package com.example.jaringobi.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.databinding.ActivitySplashBinding

@Suppress("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 저장된 정보 있으면 메인으로, 아니면 시작 페이지로
        moveActivity(StartActivity())
    }

    private fun moveActivity(p: Activity) {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, p::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}
