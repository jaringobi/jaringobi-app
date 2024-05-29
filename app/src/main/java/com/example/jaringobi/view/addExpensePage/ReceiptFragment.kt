package com.example.jaringobi.view.addExpensePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jaringobi.databinding.FragmentReceiptBinding

class ReceiptFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentReceiptBinding.inflate(inflater, container, false).root
    }
}
