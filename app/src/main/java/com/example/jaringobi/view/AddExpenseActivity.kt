package com.example.jaringobi.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.databinding.ActivityAddExpenseBinding

class AddExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toggleGroup.check(binding.button1.id)
        binding.ibBack.setOnClickListener {
            finish() // 메인액티비티로 뒤로가기
        }
    }
}
