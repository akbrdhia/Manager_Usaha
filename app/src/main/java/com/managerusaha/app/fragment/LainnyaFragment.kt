package com.managerusaha.app.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.animation.core.Easing
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.fragment.minor.KebijakanFragment
import com.managerusaha.app.fragment.minor.PanduanFragment
import com.managerusaha.app.fragment.minor.StokMasukkFragment
import com.managerusaha.app.fragment.minor.tentangFragment
import com.managerusaha.app.viewmodel.DashboardViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class LainnyaFragment : Fragment() {

    private val vm: DashboardViewModel by viewModels()

    private lateinit var barChart: BarChart
    private lateinit var btn_tentang : Button
    private lateinit var btn_hari : Button
    private lateinit var btn_minggu : Button
    private lateinit var btn_bulan : Button
    private lateinit var tvhari : TextView
    private lateinit var tvminggu : TextView
    private lateinit var tvbulan : TextView
    private lateinit var btn_panduan : Button
    private lateinit var btn_kebijakan : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lainnya, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inisialisasi(view)
        Setup()

    }

    private fun Setup() {
        checkStatusBar()
        setupbarchart(1)
        setupFilterButtons()
        subscribeTop3()
        vm.loadTop3(vm.startOfToday())
        vm.loadPendapatan()
        vm.pendapatanHari.observe(viewLifecycleOwner) { v ->
            tvhari.text = formatRupiahTanpaDesimal(v)
        }
        vm.pendapatanMinggu.observe(viewLifecycleOwner) { v ->
            tvminggu.text = formatRupiahTanpaDesimal(v)
        }
        vm.pendapatanBulan.observe(viewLifecycleOwner) { v ->
            tvbulan.text = formatRupiahTanpaDesimal(v)
        }
        setupclicklistener()
    }

    private fun subscribeTop3() {
        vm.top3.observe(viewLifecycleOwner) { list ->
            // prepare entries for at most 3 items
            val names  = list.map { it.nama }
            val values = list.map { it.total.toFloat() }
            updateChart(names, values)
        }

    }

    private fun setupFilterButtons() {
        btn_hari.setOnClickListener  { vm.loadTop3(vm.startOfToday()) }
        btn_minggu.setOnClickListener{ vm.loadTop3(vm.startOfWeek()) }
        btn_bulan.setOnClickListener { vm.loadTop3(vm.startOfMonth()) }

    }

    private fun setupclicklistener() {
        btn_tentang.setOnClickListener {
            (activity as MainActivity).replaceFragment(tentangFragment(), "TENTANG")
        }
        btn_panduan.setOnClickListener {
            (activity as MainActivity).replaceFragment(PanduanFragment(), "PANDUAN")
        }
        btn_kebijakan.setOnClickListener {
            (activity as MainActivity).replaceFragment(KebijakanFragment(), "KEBIJAKAN")
        }
    }

    private fun setupbarchart(dummynumber: Int) {
        barChart.description.isEnabled = false
        barChart.animateY(800)
        barChart.legend.isEnabled = false
        // Styling Chart
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setFitBars(true)
        barChart.setTouchEnabled(false)
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.setDrawBorders(false)
        barChart.description.isEnabled = false

        // Left Axis
        barChart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textColor = Color.DKGRAY
            textSize = 12f
        }

        // Right Axis dimatikan
        barChart.axisRight.isEnabled = false

        // X Axis
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textColor = Color.DKGRAY
            textSize = 12f
            granularity = 1f
        }
    }
    private fun updateChart(names: List<String>, values: List<Float>){
        // 1) build entries
        val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
         // 2) DataSet
        val ds = BarDataSet(entries, "Terjual").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            valueTextSize = 12f
        }
        // 3) BarData
        barChart.data = BarData(ds).apply { barWidth = 0.5f }
        // 4) X‑axis labels
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(names)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
        }
        // 5) Refresh
        barChart.animateY(800)
        barChart.notifyDataSetChanged()
        barChart.invalidate()

    }
    private fun inisialisasi(view: View) {
        barChart = view.findViewById(R.id.barChart)
        btn_tentang = view.findViewById(R.id.btn_tentang_aplikasi)
        btn_hari = view.findViewById(R.id.btn_hari)
        btn_minggu = view.findViewById(R.id.btn_minggu)
        btn_bulan = view.findViewById(R.id.btn_bulan)
        tvhari = view.findViewById(R.id.tvhari)
        tvminggu = view.findViewById(R.id.tvminggu)
        tvbulan = view.findViewById(R.id.tvbulan)
        btn_panduan = view.findViewById(R.id.btn_panduan_penggunaan)
        btn_kebijakan = view.findViewById(R.id.btn_kebijakan)
    }

    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        window.decorView.systemUiVisibility = 0
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
}