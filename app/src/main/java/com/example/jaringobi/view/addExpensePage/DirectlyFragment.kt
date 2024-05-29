package com.example.jaringobi.view.addExpensePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jaringobi.databinding.FragmentDirectlyBinding

class DirectlyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDirectlyBinding.inflate(inflater, container, false).root
    }
}
