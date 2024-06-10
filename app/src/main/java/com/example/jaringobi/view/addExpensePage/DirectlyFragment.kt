package com.example.jaringobi.view.addExpensePage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.FragmentDirectlyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DirectlyFragment : Fragment() {
    private lateinit var binding: FragmentDirectlyBinding

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDirectlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {
            addExpense()
        }
    }

    private fun addExpense() {
        val datePicker = binding.datePicker
        val storeEditText = binding.inputStore
        val costEditText = binding.inputCost
        val year = datePicker.year.toString().takeLast(2)
        val month = datePicker.month + 1
        val dayOfMonth = datePicker.dayOfMonth

        val date = "$year-${month.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
        val store = storeEditText.text.toString()
        val cost = costEditText.text.toString().toIntOrNull()

        if (store.isBlank() || cost == null || cost <= 0) {
            return Toast.makeText(requireContext(), "모든 항목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }

        db = AppDatabase.getInstance(requireContext())

        val entity =
            ExpenseEntity(
                date = date,
                store = store,
                cost = cost,
            )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenseDAO = db.getExpenseDAO()
                Log.d("INSERT", entity.toString())
                expenseDAO.insertExpense(entity)
            } catch (exception: Exception) {
                Log.e("DATABASE_ERROR", exception.toString())
                Toast.makeText(requireContext(), "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.inputStore.text = null
        binding.inputCost.text = null

        Toast.makeText(requireContext(), "지출내역이 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
