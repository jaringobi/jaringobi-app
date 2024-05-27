package com.example.jaringobi.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenseList")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val store: String,
    @ColumnInfo
    val cost: String,
)
