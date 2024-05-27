package com.example.jaringobi.data.db

import android.provider.BaseColumns

object Contract {
    const val DATABASE_NAME = "jaringobi.db"
    const val DATABASE_VERSION = 1

    object GoalEntry : BaseColumns {
        private const val TABLE_NAME = "goals"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_MONTH = "month"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_CURRENT_SPENT = "current_spent"

        const val CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME" +
                "(" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_YEAR INTEGER NOT NULL, " +
                "$COLUMN_MONTH INTEGER NOT NULL, " +
                "$COLUMN_AMOUNT INTEGER NOT NULL, " +
                "$COLUMN_CURRENT_SPENT INTEGER NOT NULL DEFAULT 0" +
                ")"

        const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    object ExpenseEntry : BaseColumns {
        private const val TABLE_NAME = "expenses"
        private const val COLUMN_PLACE = "place"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"

        const val CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME" +
                "(" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PLACE TEXT NOT NULL," +
                "$COLUMN_AMOUNT INTEGER NOT NULL," +
                "$COLUMN_DATE TEXT NOT NULL" +
                ")"

        const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
