package com.example.jaringobi.view.expenseListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.jaringobi.R
import com.example.jaringobi.databinding.ItemCalendarDayBinding

/**
 * RecyclerView를 사용하여 날짜 목록을 표시
 *
 * @param dates 표시할 날짜 목록.
 * @param onDateClick 날짜 클릭 이벤트를 처리하는 콜백 함수
 */
class CalendarAdapter(
    private val dates: List<Int>,
    private val onDateClick: (Int) -> Unit,
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

    /**
     * 달력 항목을 위한 ViewHolder
     */
    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 날짜를 TextView에 바인딩하고 클릭 리스너를 설정
         *
         * @param date 바인딩할 날짜.
         * @param position 어댑터에서 항목의 위치.
         */
        fun bind(date: Int, position: Int) {
            binding.calendarDayText.text = date.toString()
            setWeekendColor(binding.calendarDayText, position)
            setSelectionBackground(position)

            binding.root.setOnClickListener {
                onDateClick(date)
                updateSelection(position)
            }
        }

        /**
         * 선택된 항목의 배경을 업데이트
         *
         * @param position 어댑터에서 항목의 위치.
         */
        private fun setSelectionBackground(position: Int) {
            if (selectedPosition == position) {
                binding.calendarDayText.background = ContextCompat.getDrawable(binding.root.context, R.drawable.selected_date_background)
            } else {
                binding.calendarDayText.background = null
            }
        }

        /**
         * 주말의 텍스트 색상을 설정
         *
         * @param textView 색상을 설정할 TextView.
         * @param position 어댑터에서 항목의 위치.
         */
        private fun setWeekendColor(textView: TextView, position: Int) {
            val dayOfWeek = (position + 1) % 7
            val colorResId = when (dayOfWeek) {
                0 -> R.color.sunday_red
                6 -> R.color.saturday_blue
                else -> R.color.default_text_color
            }
            textView.setTextColor(ContextCompat.getColor(textView.context, colorResId))
        }

        /**
         * 선택된 위치를 업데이트하고 어댑터에 알림
         *
         * @param newPosition 새로운 선택된 위치.
         */
        private fun updateSelection(newPosition: Int) {
            notifyItemChanged(selectedPosition) // 이전 선택된 항목 갱신
            selectedPosition = newPosition
            notifyItemChanged(selectedPosition) // 현재 선택된 항목 갱신
        }
    }
}
