package com.example.jaringobi.view.expenseListPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.jaringobi.R
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseDAO
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ActivityExpensesListBinding
import com.example.jaringobi.model.CalendarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ExpenseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesListBinding
    private lateinit var expenseDAO: ExpenseDAO
    private var currentYear = 2024
    private var currentMonth = 6 // 6월로 설정
    private var selectedDate: LocalDate? = null

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

        // 요일과 날짜 리스트 생성
        val datesWithWeekdays = getDatesWithWeekdays()
        // 캘린더 그리드 레이아웃 설정
        binding.calendarGrid.layoutManager = GridLayoutManager(this, 7)
        // 캘린더 어댑터 설정
        binding.calendarGrid.adapter =
            CalendarAdapter(datesWithWeekdays) { day ->
                try {
                    val selectedDate = LocalDate.of(currentYear, currentMonth, day)
                    loadExpensesByDate(selectedDate)
                } catch (e: DateTimeException) {
                    // Invalid date, ignore
                }
            }

        // PagerSnapHelper 설정
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarGrid)
    }

    // 요일과 날짜 리스트 생성
    private fun getDatesWithWeekdays(): List<CalendarItem> {
        val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")
        val calendar =
            Calendar.getInstance().apply {
                set(currentYear, currentMonth - 1, 1) // 월은 0부터 시작하므로 1을 빼줌
            }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1은 일요일

        val calendarItems = mutableListOf<CalendarItem>()
        calendarItems.addAll(weekdays.map { CalendarItem.Weekday(it) })

        // 첫 주의 공백 추가
        for (i in 1 until firstDayOfWeek) {
            calendarItems.add(CalendarItem.Empty)
        }

        // 날짜 추가
        for (day in 1..daysInMonth) {
            calendarItems.add(CalendarItem.Date(day))
        }

        return calendarItems
    }

    private fun updateMonthDisplay() {
        // 현재 월을 텍스트 뷰에 표시
        binding.currentMonth.text = getString(R.string.month_display, currentMonth)
    }

    private fun changeMonth(delta: Int) {
        // 월 변경
        currentMonth += delta
        if (currentMonth < 1) {
            currentMonth = 12
            currentYear--
        } else if (currentMonth > 12) {
            currentMonth = 1
            currentYear++
        }
        // 월 표시 업데이트 및 지출 목록 로드
        updateMonthDisplay()
        clearExpensesList() // 달 변경 시 지출 목록 초기화
        val datesWithWeekdays = getDatesWithWeekdays()
        binding.calendarGrid.adapter =
            CalendarAdapter(datesWithWeekdays) { day ->
                try {
                    val selectedDate = LocalDate.of(currentYear, currentMonth, day)
                    loadExpensesByDate(selectedDate)
                } catch (e: DateTimeException) {
                    // Invalid date, ignore
                }
            }
    }

    private fun loadExpensesByMonth(month: Int) {
        // 지정된 월의 지출 목록 로드
        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = expenseDAO.getExpensesByMonth(String.format(Locale.KOREA, "%02d", month))
            withContext(Dispatchers.Main) {
                // ExpenseAdapter 호출 시 삭제 콜백 추가
                binding.expensesList.adapter =
                    ExpenseAdapter(expenses) { expense ->
                        deleteExpense(expense)
                    }
            }
        }
    }

    private fun loadExpensesByDate(date: LocalDate) {
        // 지정된 날짜의 지출 목록 로드
        lifecycleScope.launch(Dispatchers.IO) {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
            val expenses = expenseDAO.getExpensesByDate(formattedDate)
            withContext(Dispatchers.Main) {
                // ExpenseAdapter 호출 시 삭제 콜백 추가
                binding.expensesList.adapter =
                    ExpenseAdapter(expenses) { expense ->
                        deleteExpense(expense)
                    }
            }
        }
    }

    private fun deleteExpense(expense: ExpenseEntity) {
        // 지출 항목 삭제
        lifecycleScope.launch(Dispatchers.IO) {
            expenseDAO.deleteExpenseById(expense.id) // 개별 항목 삭제
        }
    }

    private fun clearExpensesList() {
        // 지출 목록 초기화 시 삭제 콜백 제공
        binding.expensesList.adapter = ExpenseAdapter(emptyList()) { }
    }
}
