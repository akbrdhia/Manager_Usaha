package com.managerusaha.app.fragment.minor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.fragment.LainnyaFragment

class KebijakanFragment : Fragment() {
    private lateinit var groupBack: FrameLayout
    private lateinit var ivBack: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kebijakan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inisialisasi(view)
        setupClickListeners()
    }
    private fun setupClickListeners() {
        groupBack.setOnClickListener { navigateBack() }
        ivBack.setOnClickListener { navigateBack() }
    }

    private fun inisialisasi(view: View) {
        groupBack = view.findViewById(R.id.group_back)
        ivBack = view.findViewById(R.id.iv_back)
    }

    private fun navigateBack() {
        (activity as MainActivity).replaceFragment(LainnyaFragment(), "LAINNYA")
    }

}