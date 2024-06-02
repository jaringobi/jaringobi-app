package com.example.jaringobi.view.expenseListPage

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
import com.example.jaringobi.databinding.DialogEditExpenseBinding
import com.example.jaringobi.model.CalendarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ExpenseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesListBinding
    private lateinit var expenseDAO: ExpenseDAO
    private var currentYear = 2024
    private var currentMonth = 6 // 6월로 설정
    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener { finish() }

        // 데이터베이스 초기화
        expenseDAO = AppDatabase.getInstance(this).getExpenseDAO()

        setupRecyclerView()
        setupCalendar()
        clearExpensesList()
    }

    private fun setupRecyclerView() {
        binding.expensesList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCalendar() {
        updateMonthDisplay()

        binding.prevMonth.setOnClickListener { changeMonth(-1) }
        binding.nextMonth.setOnClickListener { changeMonth(1) }

        val datesWithWeekdays = getDatesWithWeekdays()
        binding.calendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.calendarGrid.adapter =
            CalendarAdapter(datesWithWeekdays) { day ->
                try {
                    val selectedDate = LocalDate.of(currentYear, currentMonth, day)
                    loadExpensesByDate(selectedDate)
                } catch (e: DateTimeException) {

                }
            }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarGrid)
    }

    private fun getDatesWithWeekdays(): List<CalendarItem> {
        val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")
        val calendar = Calendar.getInstance().apply { set(currentYear, currentMonth - 1, 1) }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val calendarItems = mutableListOf<CalendarItem>()
        calendarItems.addAll(weekdays.map { CalendarItem.Weekday(it) })

        for (i in 1 until firstDayOfWeek) {
            calendarItems.add(CalendarItem.Empty)
        }

        for (day in 1..daysInMonth) {
            calendarItems.add(CalendarItem.Date(day))
        }

        return calendarItems
    }

    private fun updateMonthDisplay() {
        binding.currentMonth.text = getString(R.string.month_display, currentMonth)
    }

    private fun changeMonth(delta: Int) {
        currentMonth += delta
        if (currentMonth < 1) {
            currentMonth = 12
            currentYear--
        } else if (currentMonth > 12) {
            currentMonth = 1
            currentYear++
        }
        updateMonthDisplay()
        clearExpensesList() // 달 변경 시 지출 목록 초기화
        val datesWithWeekdays = getDatesWithWeekdays()
        binding.calendarGrid.adapter =
            CalendarAdapter(datesWithWeekdays) { day ->
                try {
                    val selectedDate = LocalDate.of(currentYear, currentMonth, day)
                    loadExpensesByDate(selectedDate)
                } catch (e: DateTimeException) {

                }
            }
    }

//    private fun loadExpensesByMonth(month: Int) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val expenses = expenseDAO.getExpensesByMonth(String.format(Locale.KOREA, "%02d", month))
//            withContext(Dispatchers.Main) {
//                // ExpenseAdapter 호출 시 삭제 콜백 추가
//                binding.expensesList.adapter =
//                    ExpenseAdapter(expenses.toMutableList(), ::onEditClick, ::onDeleteClick)
//            }
//        }
//    }

    private fun loadExpensesByDate(date: LocalDate) {
        lifecycleScope.launch(Dispatchers.IO) {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
            val expenses = expenseDAO.getExpensesByDate(formattedDate)
            withContext(Dispatchers.Main) {
                // ExpenseAdapter 호출 시 삭제 콜백 추가
                binding.expensesList.adapter =
                    ExpenseAdapter(expenses.toMutableList(), ::onEditClick, ::onDeleteClick)
            }
        }
    }

    private fun deleteExpense(expense: ExpenseEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            expenseDAO.deleteExpenseById(expense.id) // 개별 항목 삭제
            withContext(Dispatchers.Main) {
                selectedDate?.let { loadExpensesByDate(it) } // 삭제 후 해당 날짜의 지출 내역만 로드
            }
        }
    }

    private fun clearExpensesList() {
        // 지출 목록 초기화 시 삭제 콜백
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                binding.expensesList.adapter =
                    ExpenseAdapter(emptyList<ExpenseEntity>().toMutableList(), ::onEditClick, ::onDeleteClick)
            }
        }
    }

    private fun onEditClick(expense: ExpenseEntity) {
        val dialogBinding = DialogEditExpenseBinding.inflate(layoutInflater)
        dialogBinding.editExpenseDate.setText(expense.date)
        dialogBinding.editExpenseName.setText(expense.store)
        dialogBinding.editExpenseAmount.setText(expense.cost)

        val dialog =
            AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .create()

        dialogBinding.btnUpdate.setOnClickListener {
            val updatedExpense =
                expense.copy(
                    date = dialogBinding.editExpenseDate.text.toString(),
                    store = dialogBinding.editExpenseName.text.toString(),
                    cost = dialogBinding.editExpenseAmount.text.toString(),
                )
            updateExpense(updatedExpense)
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateExpense(expense: ExpenseEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            expenseDAO.updateExpense(expense)
            withContext(Dispatchers.Main) {
                selectedDate?.let { loadExpensesByDate(it) }
            }
        }
    }

//    private fun loadExpenses() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val expenses = expenseDAO.getExpenseList()
//            withContext(Dispatchers.Main) {
//                binding.expensesList.adapter =
//                    ExpenseAdapter(expenses.toMutableList(), ::onEditClick, ::onDeleteClick)
//            }
//        }
//    }

    // onDeleteClick 함수 추가
    private fun onDeleteClick(expense: ExpenseEntity) {
        deleteExpense(expense)
    }
}
