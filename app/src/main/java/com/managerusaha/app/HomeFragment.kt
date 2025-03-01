package com.managerusaha.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatusBar()
        handle()
    }

    fun handle() {
        handle_btn_h_display()
    }

    fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        window.decorView.systemUiVisibility = 0
    }








    private fun handle_btn_h_display() {
        val btnp = view?.findViewById<Button>(R.id.btn_h_display)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        btnp?.setOnClickListener {
            (activity as MainActivity).replaceFragment(LainnyaFragment(), "LAINNYA")

            bottomNav.selectedItemId = R.id.nav_lainnya
        }
    }


}


