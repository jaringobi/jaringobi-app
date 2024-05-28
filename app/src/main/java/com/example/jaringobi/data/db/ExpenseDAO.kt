package com.example.jaringobi.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDAO {
    @Query("SELECT * FROM expenseList")
    fun getExpenseList(): List<ExpenseEntity>    // expenseList 전체 조회

    @Insert
    fun insertExpense(expenses: ExpenseEntity) // 지출 내역 추가

    @Query("DELETE FROM expenseList")
    fun deleteAllExpenses() // expenseList의 모든 행 삭제

    @Query("SELECT * FROM expenseList WHERE SUBSTR(date, 4, 2) = :month")
    fun getExpensesByMonth(month: String): List<ExpenseEntity> // 특정 월의 데이터 조회
}
