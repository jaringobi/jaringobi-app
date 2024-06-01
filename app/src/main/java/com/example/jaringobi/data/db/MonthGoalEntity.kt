package com.example.jaringobi.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthGoal")
data class MonthGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo
    val month: String,
    @ColumnInfo
    val goal: Int,
)
