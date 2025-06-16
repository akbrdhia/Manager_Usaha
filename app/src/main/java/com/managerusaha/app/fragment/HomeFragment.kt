package com.managerusaha.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.fragment.minor.StokKeluarrFragment
import com.managerusaha.app.fragment.minor.StokMasukkFragment
import com.managerusaha.app.viewmodel.riwayatViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private val vm: riwayatViewModel by viewModels()

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
        handle(view)
    }

    fun handle(view : View) {
        handle_btn_Stok_Masuk()
        handle_btn_stok_keluar()
        handle_dashboard(view)
    }

    fun formatRupiahTanpaDesimal(value: Double): String {
        // Buat simbol khusus: Rp<spasi>
        val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            currencySymbol = "Rp "
            groupingSeparator = '.'
            // decimalSeparator akan diabaikan karena kita set no fraction
        }

        // Ambil instance DecimalFormat untuk currency, cast ke DecimalFormat
        val df = DecimalFormat.getCurrencyInstance(Locale("id", "ID")) as DecimalFormat
        df.decimalFormatSymbols = symbols
        df.maximumFractionDigits = 0
        df.minimumFractionDigits = 0
        return df.format(value)
    }

    private fun handle_dashboard(view: View) {
        // Panggil load
        vm.loadStatistikBulanIni()

        // Observe dan tampilkan ke UI
        vm.stokMasuk.observe(viewLifecycleOwner) { jumlah ->
            view.findViewById<TextView>(R.id.tvstokmasuk).text = jumlah.toString()
        }
        vm.stokKeluar.observe(viewLifecycleOwner) { jumlah ->
            view.findViewById<TextView>(R.id.tvstokkeluar).text = jumlah.toString()
        }
        vm.omset.observe(viewLifecycleOwner) { omset ->
            val txt = view.findViewById<TextView>(R.id.tvomset)
            txt.text = formatRupiahTanpaDesimal(omset)
        }
        vm.untung.observe(viewLifecycleOwner) { untung ->
            val txt = view.findViewById<TextView>(R.id.tvuntung)
            txt.text = formatRupiahTanpaDesimal(untung)
        }
    }

    private fun handle_btn_stok_keluar() {
        val btnp = view?.findViewById<Button>(R.id.btn_stok_keluar)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        btnp?.setOnClickListener {
            (activity as MainActivity).replaceFragment(StokKeluarrFragment(), "STOK KELUAR")
        }
    }

    fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        window.decorView.systemUiVisibility = 0
    }

    private fun handle_btn_Stok_Masuk() {
        val btnp = view?.findViewById<Button>(R.id.btn_stok_masuk)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        btnp?.setOnClickListener {
            (activity as MainActivity).replaceFragment(StokMasukkFragment(), "STOK MASUK")
        }
    }


}


