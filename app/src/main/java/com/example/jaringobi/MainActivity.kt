package com.example.jaringobi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.databinding.ActivityMainBinding
import com.example.jaringobi.db.DBHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)
    }
}
