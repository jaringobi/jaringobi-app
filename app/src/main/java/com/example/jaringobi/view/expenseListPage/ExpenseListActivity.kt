package com.example.jaringobi.view.expenseListPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jaringobi.R
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseDAO
import com.example.jaringobi.databinding.ActivityExpensesListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ExpenseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesListBinding
    private lateinit var expenseDAO: ExpenseDAO
    private var currentMonth = LocalDate.now().monthValue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityExpensesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기
        binding.ibBack.setOnClickListener {
            finish()
        }

        // 데이터베이스 초기화
        expenseDAO = AppDatabase.getInstance(this).getExpenseDAO()

        // 리사이클러뷰 설정
        setupRecyclerView()
        // 캘린더 설정
        setupCalendar()
        // 지출 목록 초기화
        clearExpensesList()
    }

    private fun setupRecyclerView() {
        binding.expensesList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCalendar() {
        // 현재 월 표시
        updateMonthDisplay()

        // 이전 달 버튼 클릭 리스너 설정
        binding.prevMonth.setOnClickListener {
            changeMonth(-1)
        }

        // 다음 달 버튼 클릭 리스너 설정
        binding.nextMonth.setOnClickListener {
            changeMonth(1)
        }

        // 현재 월의 날짜 리스트 생성
        val dates = getDatesForCurrentMonth()
        // 캘린더 그리드 레이아웃 설정
        binding.calendarGrid.layoutManager = GridLayoutManager(this, 7)
        // 캘린더 어댑터 설정
        binding.calendarGrid.adapter =
            CalendarAdapter(dates) { day ->
                val selectedDate = LocalDate.of(LocalDate.now().year, currentMonth, day)
                loadExpensesByDate(selectedDate)
            }
    }

    private fun updateMonthDisplay() {
        // 현재 월을 텍스트 뷰에 표시
        binding.currentMonth.text = getString(R.string.month_display, currentMonth)
    }

    private fun getDatesForCurrentMonth(): List<Int> {
        // 현재 월의 날짜 리스트 반환
        val yearMonth = YearMonth.of(LocalDate.now().year, currentMonth)
        return (1..yearMonth.lengthOfMonth()).toList()
    }

    private fun changeMonth(delta: Int) {
        // 월 변경
        currentMonth += delta
        if (currentMonth < 1) {
            currentMonth = 12
        } else if (currentMonth > 12) {
            currentMonth = 1
        }
        // 월 표시 업데이트 및 지출 목록 로드
        updateMonthDisplay()
        loadExpensesByMonth(currentMonth)
    }

    private fun loadExpensesByMonth(month: Int) {
        // 지정된 월의 지출 목록 로드
        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = expenseDAO.getExpensesByMonth(String.format("%02d", month))
            withContext(Dispatchers.Main) {
                binding.expensesList.adapter = ExpenseAdapter(expenses)
            }
        }
    }

    private fun loadExpensesByDate(date: LocalDate) {
        // 지정된 날짜의 지출 목록 로드
        lifecycleScope.launch(Dispatchers.IO) {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
            val expenses = expenseDAO.getExpensesByDate(formattedDate)
            withContext(Dispatchers.Main) {
                binding.expensesList.adapter = ExpenseAdapter(expenses)
            }
        }
    }

    private fun clearExpensesList() {
        // 지출 목록 초기화
        binding.expensesList.adapter = ExpenseAdapter(emptyList())
    }
}
