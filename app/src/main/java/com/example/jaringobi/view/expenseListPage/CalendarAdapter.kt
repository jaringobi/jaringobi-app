package com.example.jaringobi.view.expenseListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.jaringobi.R
import com.example.jaringobi.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private val dates: List<Int>,
    private val onDateClick: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dates[position], position)
    }

    override fun getItemCount() = dates.size

    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Int, position: Int) {
            binding.calendarDayText.text = day.toString()
            setWeekendColor(binding.calendarDayText, position)

            binding.root.setOnClickListener {
                onDateClick(day)
                notifyItemChanged(selectedPosition) // 이전 선택된 항목 갱신
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition) // 현재 선택된 항목 갱신
            }

            if (selectedPosition == position) {
                binding.calendarDayText.background = ContextCompat.getDrawable(binding.root.context, R.drawable.selected_date_background)
            } else {
                binding.calendarDayText.background = null
            }
        }

        private fun setWeekendColor(textView: TextView, position: Int) {
            val dayOfWeek = (position + 1) % 7
            when (dayOfWeek) {
                0 -> textView.setTextColor(ContextCompat.getColor(textView.context, R.color.sunday_red))
                6 -> textView.setTextColor(ContextCompat.getColor(textView.context, R.color.saturday_blue))
                else -> textView.setTextColor(ContextCompat.getColor(textView.context, R.color.default_text_color))
            }
        }
    }
}
