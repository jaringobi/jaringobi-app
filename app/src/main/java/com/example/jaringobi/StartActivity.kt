package com.example.jaringobi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity(), OnGoalSetListener {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSelectGoal.setOnClickListener {
            val dialog = StartDialog()
            dialog.listener = this // 리스너 설정
            dialog.show(supportFragmentManager, "StartDialog")
        }

        binding.btnStart.setOnClickListener {
            moveActivity(MainActivity())
        }
    }

    private fun moveActivity(p: Activity) {
        val intent = Intent(this, p::class.java)
        startActivity(intent)
        finish()
    }

    override fun onGoalSet(monthGoal: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("monthGoal", monthGoal)
        startActivity(intent)
    }
}
