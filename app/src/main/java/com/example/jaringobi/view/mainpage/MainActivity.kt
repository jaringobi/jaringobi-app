package com.example.jaringobi.view.mainpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jaringobi.view.custom.OnGoalSetListener
import com.example.jaringobi.view.custom.StartDialog
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseDAO
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ActivityMainBinding
import com.example.jaringobi.view.addExpensePage.AddExpenseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), OnGoalSetListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var expenseDAO: ExpenseDAO
    private lateinit var db: AppDatabase


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

        db = AppDatabase.getInstance(this)
        // 더미 데이터 삽입
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val expenseDAO = db.getExpenseDAO()
                db.getExpenseDAO().deleteAllExpenses()

                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24.05.27(월)", store = "맘스터치", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24.05.26(일)", store = "이마트 에브리데이", cost = "8,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24.05.25(토)", store = "메머드커피", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24.05.25(토)", store = "메가커피", cost = "2,000 원"),
                )

                val expenseList = expenseDAO.getExpenseList()
                withContext(Dispatchers.Main) {
                    val adapter = MainLvAdapter(this@MainActivity, expenseList)
                    binding.lvRecentCost.adapter = adapter
                }
            }
        }

//        val adapter = MainLvAdapter(this, expenseList)
//        binding.lvRecentCost.adapter = adapter
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
