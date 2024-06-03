package com.example.jaringobi.view.mainPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ItemRecentCostBinding

class MainLvAdapter(private val context: Context, private val costList: List<ExpenseEntity>) : BaseAdapter() {
    private lateinit var binding: ItemRecentCostBinding

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        binding = ItemRecentCostBinding.inflate(LayoutInflater.from(context))

        val recentCost = costList[position]
        with(binding) {
            tvRecentCostDate.text = recentCost.date
            tvRecentCostStore.text = recentCost.store
            tvRecentCostMoney.text = recentCost.cost
        }
        return binding.root
    }

    override fun getItem(position: Int): Any {
        return costList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return if (costList.size > 3) 3 else costList.size
    }
}
