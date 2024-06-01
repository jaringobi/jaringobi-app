package com.example.jaringobi.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDAO {
    @Query("SELECT * FROM expenseList")
    fun getExpenseList(): List<ExpenseEntity> // expenseList 전체 조회

    @Insert
    fun insertExpense(expenses: ExpenseEntity) // 지출 내역 추가

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity) // 삭제 메소드 추가

    @Query("DELETE FROM expenseList WHERE id = :expenseId") // ID를 기반으로 특정 지출 내역 삭제
    fun deleteExpenseById(expenseId: Long)

    @Query("DELETE FROM expenseList")
    fun deleteAllExpenses() // expenseList 모든 행 삭제

    @Query("SELECT * FROM expenseList WHERE SUBSTR(date, 4, 2) = :month")
    fun getExpensesByMonth(month: String): List<ExpenseEntity> // 특정 월의 데이터 조회

    @Query("SELECT * FROM expenseList WHERE date = :date")
    fun getExpensesByDate(date: String): List<ExpenseEntity> // 특정 날짜의 데이터 조회
}
