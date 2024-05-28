package com.example.jaringobi.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.jaringobi.data.db.Contract
import com.example.jaringobi.data.db.Contract.ExpenseEntry
import com.example.jaringobi.data.db.Contract.GoalEntry

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    Contract.DATABASE_NAME,
    null,
    Contract.DATABASE_VERSION,
) {
    override fun onCreate(db: SQLiteDatabase?) {
        Thread {
            db?.execSQL(ExpenseEntry.CREATE_TABLE)
            db?.execSQL(GoalEntry.CREATE_TABLE)
        }.start()
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int,
    ) {
        Thread {
            db?.execSQL(ExpenseEntry.DROP_TABLE)
            db?.execSQL(GoalEntry.DROP_TABLE)
        }.start()

        onCreate(db)
    }
}
