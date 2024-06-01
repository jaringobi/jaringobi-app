package com.example.jaringobi.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jaringobi.databinding.ActivityStartBinding
import com.example.jaringobi.view.custom.OnGoalSetListener
import com.example.jaringobi.view.custom.StartDialog
import com.example.jaringobi.view.mainpage.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class StartActivity : AppCompatActivity(), OnGoalSetListener {
    private lateinit var binding: ActivityStartBinding
    private lateinit var db: AppDatabase
    private var isMonthGoal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        db = AppDatabase.getInstance(this)


        // 현재 년도와 월을 가져옴
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) + 1
        val monthString = if (month < 10) "0$month" else month.toString()
        val dateString = "${year}-${monthString}"
        checkMonthGoal(year, month)

        binding.btnSelectGoal.setOnClickListener {
            val dialog = StartDialog(dateString, isMonthGoal)
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

    override fun onGoalSet(date: String, monthGoal: Int) {
        val goalDAO = db.goalDAO()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                goalDAO.insertMonthGoal(MonthGoalEntity(month = date, goal = monthGoal))
            }
        }
        moveActivity(MainActivity())
    }

    override fun onGoalModify(date: String, monthGoal: Int) {
        val goalDAO = db.goalDAO()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                goalDAO.updateMonthGoal(date, monthGoal)
            }
        }
        moveActivity(MainActivity())
    }

    private fun checkMonthGoal(year: Int, month: Int) {
        val goalDAO = db.goalDAO()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val monthString = if (month < 10) "0$month" else month.toString()
                val dateString = "$year-$monthString"
                val monthGoal = goalDAO.getMonthGoal(dateString)

                // 메인 스레드에서 UI 업데이트
                withContext(Dispatchers.Main) {
                    if (monthGoal != null) {
                        binding.btnSelectGoal.text = getString(R.string.text_modify_goal)
                        isMonthGoal = true
                    } else {
                        isMonthGoal = false
                    }
                }
            }
        }
    }
}
