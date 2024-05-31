package com.example.jaringobi.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GoalDAO {
    @Insert
    fun insertMonthGoal(monthGoal: MonthGoalEntity) // 지출 목표 없을 시 추가

    @Query("UPDATE monthGoal SET goal = :goal WHERE month = :month")
    fun updateMonthGoal(month: String, goal: Int) // 해당 월의 지출 목표 수정

    @Query("SELECT goal FROM monthGoal WHERE month = :month")
    fun getMonthGoal(month: String): Int?   // 특정 월의 월 지출 목표 조회, 없을 수도 있으니 Int?로 설정
}

