package com.example.jaringobi.view.mainPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.ItemRecentCostBinding
import java.text.DecimalFormat

class MainLvAdapter(private val context: Context, private val costList: List<ExpenseEntity>) : BaseAdapter() {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        val binding: ItemRecentCostBinding
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            binding = ItemRecentCostBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            viewHolder = ViewHolder(binding)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val decimalFormat = DecimalFormat("#,###")

        val recentCost = costList[position]
        with(viewHolder.binding) {
            tvRecentCostDate.text = recentCost.date
            tvRecentCostStore.text = recentCost.store
            tvRecentCostMoney.text = decimalFormat.format(recentCost.cost) + " 원"
        }

        return view
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

    private class ViewHolder(val binding: ItemRecentCostBinding)
}
