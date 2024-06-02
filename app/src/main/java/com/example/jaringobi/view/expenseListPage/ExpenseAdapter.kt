package com.example.jaringobi.view.expenseListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private var expenses: List<ExpenseEntity>,
    private val onDeleteClick: (ExpenseEntity) -> Unit,
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int,
    ) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: ExpenseEntity) {
            binding.expenseDate.text = expense.date
            binding.expenseName.text = expense.store
            binding.expenseAmount.text = expense.cost
            // 삭제 버튼 클릭 리스너 설정
            binding.btnDelete.setOnClickListener {
                onDeleteClick(expense) // 삭제 콜백 호출
                // UI 즉시 업데이트: 클릭된 항목을 리스트에서 제거
                expenses = expenses.filter { it.id != expense.id }
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, expenses.size)

                // 삭제 시 토스트 메시지 표시
                Toast.makeText(binding.root.context, "삭제 완료", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
