package com.example.jaringobi.view.expenseListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private var expenses: MutableList<ExpenseEntity>,
    private val onEditClick: (ExpenseEntity) -> Unit,
    private val onDeleteClick: (ExpenseEntity) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // 데이터를 업데이트하고 어댑터에 변경 사항을 알리는 메서드 추가
    fun updateExpenses(newExpenses: List<ExpenseEntity>) {
        expenses.clear()
        expenses.addAll(newExpenses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseEntity) {
            binding.expenseDate.text = expense.date
            binding.expenseName.text = expense.store
            binding.expenseAmount.text = expense.cost.toString()

            binding.btnDelete.setOnClickListener {
                onDeleteClick(expense)
                expenses = expenses.filter { it.id != expense.id }.toMutableList()
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, expenses.size)
                Toast.makeText(binding.root.context, "삭제 완료", Toast.LENGTH_SHORT).show()
            }

            binding.btnEdit.setOnClickListener {
                onEditClick(expense)
            }
        }
    }
}
