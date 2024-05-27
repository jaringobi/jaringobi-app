package com.example.jaringobi.view.mainpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.OnGoalSetListener
import com.example.jaringobi.StartDialog
import com.example.jaringobi.data.RecentCost
import com.example.jaringobi.databinding.ActivityMainBinding
import com.example.jaringobi.view.AddExpenseActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), OnGoalSetListener {
    private lateinit var binding: ActivityMainBinding
    var costList =
        arrayListOf<RecentCost>(
            RecentCost("24.05.20(월)", "맘스터치", "8,600 원"),
            RecentCost("24.05.19(일)", "이마트 에브리데이", "11,200 원"),
            RecentCost("24.05.18(토)", "보이스피싱", "2,190,600 원"),
            RecentCost("24.05.18(토)", "메머드커피", "1,600 원"),
        )
//    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addOnBackPressedCallback()

        val monthGoal = intent.getStringExtra("monthGoal")
        Log.d("MainActivity", "Month Goal: $monthGoal")

        updateUI(monthGoal)

        binding.btnSetGoal.setOnClickListener {
            val dialog = StartDialog()
            dialog.listener = this // 리스너 설정
            dialog.show(supportFragmentManager, "StartDialog")
        }

        val adapter = MainLvAdapter(this, costList)
        binding.lvRecentCost.adapter = adapter

        binding.btnAddList.setOnClickListener {
            moveActivity(AddExpenseActivity())
        }

//        dbHelper = DBHelper(this)
    }

    private fun addOnBackPressedCallback() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 뒤로 가기 버튼이 눌렸을 때 처리 동작
                }
            }

        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun updateUI(monthGoal: String?) {
        if (monthGoal.isNullOrEmpty()) {
            binding.layoutWithoutData.visibility = View.VISIBLE
            binding.layoutWithData.visibility = View.INVISIBLE
        } else {
            binding.tvMonthGoalData.text = monthGoal
            binding.layoutWithoutData.visibility = View.INVISIBLE
            binding.layoutWithData.visibility = View.VISIBLE
        }
    }

    override fun onGoalSet(monthGoal: String) {
        updateUI(monthGoal)
    }

    private fun moveActivity(p: Activity) {
        val intent = Intent(this, p::class.java)
        startActivity(intent)
//        finish()
    }
}
