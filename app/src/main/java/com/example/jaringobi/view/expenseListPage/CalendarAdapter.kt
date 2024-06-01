package com.example.jaringobi.view.expenseListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.jaringobi.R
import com.example.jaringobi.databinding.ItemCalendarDayBinding
import com.example.jaringobi.model.CalendarItem

/**
 * RecyclerView를 사용하여 날짜 목록을 표시
 *
 * @param dates 표시할 날짜 목록.
 * @param onDateClick 날짜 클릭 이벤트를 처리하는 콜백 함수
 */
class CalendarAdapter(
    private val items: List<CalendarItem>,
    private val onDateClick: (Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CalendarItem.Weekday -> VIEW_TYPE_WEEKDAY
            is CalendarItem.Date -> VIEW_TYPE_DATE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WEEKDAY ->
                WeekdayViewHolder(
                    ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                )

            VIEW_TYPE_DATE ->
                DateViewHolder(
                    ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                )

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is WeekdayViewHolder -> holder.bind((items[position] as CalendarItem.Weekday).name)
            is DateViewHolder -> holder.bind((items[position] as CalendarItem.Date).day, position)
        }
    }

    override fun getItemCount() = items.size

    inner class WeekdayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weekday: String) {
            binding.calendarDayText.text = weekday
            binding.calendarDayText.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.default_text_color),
            )
        }
    }

    inner class DateViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            day: Int,
            position: Int,
        ) {
            binding.calendarDayText.text = day.toString()
            setWeekendColor(binding.calendarDayText, position)
            setSelectionBackground(position)

            binding.root.setOnClickListener {
                onDateClick(day)
                updateSelection(position)
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
            val dayOfWeek = (position % 7)
            val colorResId =
                when (dayOfWeek) {
                    0 -> R.color.sunday_red // 일요일
                    6 -> R.color.saturday_blue // 토요일
                    else -> R.color.default_text_color
                }
            textView.setTextColor(ContextCompat.getColor(textView.context, colorResId))
        }

        private fun updateSelection(newPosition: Int) {
            notifyItemChanged(selectedPosition)
            selectedPosition = newPosition
            notifyItemChanged(selectedPosition)
        }
    }

    companion object {
        private const val VIEW_TYPE_WEEKDAY = 0
        private const val VIEW_TYPE_DATE = 1
    }
}
