package com.example.jaringobi.view.expenseListPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseDAO
import com.example.jaringobi.databinding.ActivityExpensesListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExpenseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesListBinding
    private lateinit var expenseDAO: ExpenseDAO
    private var currentMonth = LocalDate.now().monthValue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpensesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseDAO = AppDatabase.getInstance(this).getExpenseDAO()

        setupRecyclerView()
        setupCalendar()
        clearExpensesList()
    }

    private fun setupRecyclerView() {
        binding.expensesList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCalendar() {
        // 현재 월 설정
        binding.currentMonth.text = "${currentMonth}월"

        // 이전 달로 이동
        binding.prevMonth.setOnClickListener {
            currentMonth -= 1
            if (currentMonth < 1) {
                currentMonth = 12
            }
            binding.currentMonth.text = "${currentMonth}월"
            loadExpensesByMonth(currentMonth)
        }

        // 다음 달로 이동
        binding.nextMonth.setOnClickListener {
            currentMonth += 1
            if (currentMonth > 12) {
                currentMonth = 1
            }
            binding.currentMonth.text = "${currentMonth}월"
            loadExpensesByMonth(currentMonth)
        }

        // RecyclerView를 사용한 캘린더 설정
        val dates = (1..31).toList() // 예시로 1일부터 31일까지 날짜 리스트 생성
        binding.calendarGrid.layoutManager = GridLayoutManager(this, 7) // 7열 그리드 레이아웃
        binding.calendarGrid.adapter =
            CalendarAdapter(dates) { day ->
                val selectedDate = LocalDate.of(LocalDate.now().year, currentMonth, day)
                loadExpensesByDate(selectedDate)
            }
    }

    private fun loadExpensesByMonth(month: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val expenses = expenseDAO.getExpensesByMonth(String.format("%02d", month))
            withContext(Dispatchers.Main) {
                binding.expensesList.adapter = ExpenseAdapter(expenses)
            }
        }
    }

    private fun loadExpensesByDate(date: LocalDate) {
        GlobalScope.launch(Dispatchers.IO) {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
            val expenses = expenseDAO.getExpensesByDate(formattedDate)
            withContext(Dispatchers.Main) {
                binding.expensesList.adapter = ExpenseAdapter(expenses)
            }
        }
    }

    private fun clearExpensesList() {
        binding.expensesList.adapter = ExpenseAdapter(emptyList())
    }
}
