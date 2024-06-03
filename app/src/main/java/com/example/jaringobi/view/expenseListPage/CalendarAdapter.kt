package com.example.jaringobi.view.expenseListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.jaringobi.R
import com.example.jaringobi.data.model.CalendarItem
import com.example.jaringobi.databinding.ItemCalendarDayBinding
import com.example.jaringobi.databinding.ItemCalendarWeekdayBinding

class CalendarAdapter(
    private val items: List<CalendarItem>,
    private val onDateClick: (Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_calendar_day -> {
                val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CalendarViewHolder(binding)
            }
            R.layout.item_calendar_weekday -> {
                val binding = ItemCalendarWeekdayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                WeekdayViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is CalendarViewHolder -> holder.bind(items[position] as? CalendarItem.Date, position)
            is WeekdayViewHolder -> holder.bind(items[position] as CalendarItem.Weekday)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CalendarItem.Date -> R.layout.item_calendar_day
            is CalendarItem.Weekday -> R.layout.item_calendar_weekday
            is CalendarItem.Empty -> R.layout.item_calendar_day
        }
    }

    override fun getItemCount() = items.size

    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: CalendarItem.Date?,
            position: Int,
        ) {
            if (item != null) {
                binding.calendarDayText.text = item.day.toString()
                setWeekendColor(binding.calendarDayText, position)
                setSelectionBackground(position)

                binding.root.setOnClickListener {
                    onDateClick(item.day)
                    updateSelection(position)
                }
            } else {
                binding.calendarDayText.text = ""
                binding.root.setOnClickListener(null)
            }
        }

        private fun setSelectionBackground(position: Int) {
            binding.calendarDayText.background =
                if (selectedPosition == position) {
                    ContextCompat.getDrawable(binding.root.context, R.drawable.selected_date_background)
                } else {
                    null
                }
        }

        private fun setWeekendColor(
            textView: TextView,
            position: Int,
        ) {
            val dayOfWeek = (position % 7) + 1
            val colorResId =
                when (dayOfWeek) {
                    1 -> R.color.sunday_red // 일요일
                    7 -> R.color.saturday_blue // 토요일
                    else -> R.color.default_text_color
                }
            textView.setTextColor(ContextCompat.getColor(textView.context, colorResId))
        }

        private fun updateSelection(newPosition: Int) {
            notifyItemChanged(selectedPosition) // 이전 선택된 항목 갱신
            selectedPosition = newPosition
            notifyItemChanged(selectedPosition) // 현재 선택된 항목 갱신
        }
    }

    inner class WeekdayViewHolder(private val binding: ItemCalendarWeekdayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarItem.Weekday) {
            binding.weekdayText.text = item.name
        }
    }
}
