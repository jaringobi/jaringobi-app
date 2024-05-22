package com.example.jaringobi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.databinding.ActivityAddExpenseBinding

class AddExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
