package com.example.jaringobi.view.addExpensePage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.jaringobi.databinding.ActivityAddExpenseBinding

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toggleGroup.check(binding.button1.id)

        // 뒤로가기
        binding.ibBack.setOnClickListener {
            finish()
        }

        setFragment(ReceiptFragment())

        binding.button1.setOnClickListener {
            setFragment(ReceiptFragment())
        }

        binding.button2.setOnClickListener {
            setFragment(DirectlyFragment())
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}
