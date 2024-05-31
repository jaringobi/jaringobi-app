package com.example.jaringobi.view.mainpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jaringobi.R
import com.example.jaringobi.view.custom.OnGoalSetListener
import com.example.jaringobi.view.custom.StartDialog
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseDAO
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ActivityMainBinding
import com.example.jaringobi.view.addExpensePage.AddExpenseActivity
import com.example.jaringobi.view.expenseListPage.ExpenseListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

        var nowMonth = 5
        binding.ivLeft.setOnClickListener {
            nowMonth -= 1
            if (nowMonth < 1) {
                nowMonth = 12
            }
            binding.tvNowMonth.text = getString(R.string.text_now_month, nowMonth.toString())
            updateExpenseList(nowMonth)
        }

        binding.ivRight.setOnClickListener {
            nowMonth += 1
            if (nowMonth > 12) {
                nowMonth = 1
            }
            binding.tvNowMonth.text = getString(R.string.text_now_month, nowMonth.toString())
            updateExpenseList(nowMonth)
        }

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
                    ExpenseEntity(date = "24-06-22", store = "맘스터치", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24-04-21", store = "맘스터치", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24-04-25", store = "맘스터치", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24-04-01", store = "맘스터치", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24-05-20", store = "이마트 에브리데이", cost = "8,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24-05-25", store = "메머드커피", cost = "10,000 원"),
                )
                expenseDAO.insertExpense(
                    ExpenseEntity(date = "24-05-24", store = "메가커피", cost = "2,000 원"),
                )
                updateExpenseList(nowMonth)
            }
        }

//        val adapter = MainLvAdapter(this, expenseList)
//        binding.lvRecentCost.adapter = adapter
        binding.btnAddList.setOnClickListener {
            moveActivity(AddExpenseActivity())
        }

        // 지출내역 더보기 클릭시 지출내역 목록 페이지로 이동
        binding.btnDetailCost.setOnClickListener {
            val intent = Intent(this, ExpenseListActivity::class.java)
            startActivity(intent)
        }

//        dbHelper = DBHelper(this)
    }

    private fun updateExpenseList(month: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val expenseDAO = db.getExpenseDAO()
                val monthString = if (month < 10) "0$month" else month.toString()
                val expenseList = withContext(Dispatchers.IO) {
                    expenseDAO.getExpensesByMonth(monthString)
                }

                val prevMonth = month - 1
                val prevMonthString = if (prevMonth < 10) "0$prevMonth" else prevMonth.toString()
                val prevExpenseList = withContext(Dispatchers.IO) {
                    expenseDAO.getExpensesByMonth(prevMonthString)
                }

                // 일별로 내림차순 정렬
                val sortedExpenseList =
                    expenseList.sortedByDescending { it.date.substring(6, 8).toInt() }

                // 요일 추가
                val formattedExpenseList = sortedExpenseList.map { expense ->
                    val date =
                        LocalDate.parse(expense.date, DateTimeFormatter.ofPattern("yy-MM-dd"))
                    val dayOfWeek = date.dayOfWeek
                    val dayOfWeekKorean = when (dayOfWeek) {
                        DayOfWeek.MONDAY -> "월"
                        DayOfWeek.TUESDAY -> "화"
                        DayOfWeek.WEDNESDAY -> "수"
                        DayOfWeek.THURSDAY -> "목"
                        DayOfWeek.FRIDAY -> "금"
                        DayOfWeek.SATURDAY -> "토"
                        DayOfWeek.SUNDAY -> "일"
                    }
                    val formattedDate = "${expense.date} ($dayOfWeekKorean)"
                    expense.copy(date = formattedDate)
                }

                // 총 지출 계산
                val totalCost = formattedExpenseList.sumOf {
                    it.cost.replace(",", "").replace(" 원", "").toInt()
                }
                // 이전 달 지출 계산
                val prevTotalCost =
                    prevExpenseList.sumOf { it.cost.replace(",", "").replace(" 원", "").toInt() }

                withContext(Dispatchers.Main) {
                    val adapter = MainLvAdapter(this@MainActivity, formattedExpenseList)
                    binding.lvRecentCost.adapter = adapter

                    // 총 지출 표시
                    binding.tvTotalCost.text = getString(R.string.text_total_cost_num, totalCost)
                    var costDifference = 0

                    if (prevTotalCost > totalCost) {
                        costDifference = prevTotalCost - totalCost
                        binding.tvCompareCost.text = getString(
                            R.string.text_compare_expense,
                            costDifference.toString(), "덜"
                        )
                        binding.ivUpdown.setImageResource(R.drawable.ic_down)
                    } else {
                        costDifference = totalCost - prevTotalCost
                        binding.tvCompareCost.text = getString(
                            R.string.text_compare_expense,
                            costDifference.toString(),
                            "더"
                        )
                        binding.ivUpdown.setImageResource(R.drawable.ic_up)
                    }

                    val percentageChange = if (prevTotalCost != 0) {
                        Math.round((costDifference.toDouble() / prevTotalCost.toDouble()) * 100 * 100) / 100
                    } else {
                        0.0
                    }
                    binding.tvCompareCostPercent.text = getString(R.string.text_compare_expense_percent, percentageChange.toDouble())
                }
            }
        }
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
