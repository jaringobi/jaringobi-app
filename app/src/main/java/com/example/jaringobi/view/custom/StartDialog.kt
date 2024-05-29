package com.example.jaringobi.view.custom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.jaringobi.data.utils.GetDisplayUtil
import com.example.jaringobi.databinding.DialogSelectGoalBinding

@Suppress("DEPRECATION")
class StartDialog : DialogFragment() {
    var listener: OnGoalSetListener? = null
    private var mbinding: DialogSelectGoalBinding? = null
    private val binding get() = mbinding!!

    override fun onStart() {
        super.onStart()

        val size = GetDisplayUtil.getSize(requireContext())
        val width = (size.first * 0.8).toInt()

        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mbinding = DialogSelectGoalBinding.inflate(inflater, container, false)
        val view = binding.root

        // 확인 버튼
        binding.btnSelectOk.setOnClickListener {
            val monthGoal = binding.etMonthGoal.text.toString()
            if (monthGoal.isBlank()) {
                Toast.makeText(requireContext(), "목표 금액을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                listener?.onGoalSet(monthGoal)
            }
            dismiss()
        }

        // 취소 버튼
        binding.btnSelectNo.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }
}
