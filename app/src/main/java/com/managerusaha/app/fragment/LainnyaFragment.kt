package com.managerusaha.app.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.Easing
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.managerusaha.app.R


class LainnyaFragment : Fragment() {

    private lateinit var barChart: BarChart

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
        setupclicklistener()
    }

    private fun setupclicklistener() {

    }

    private fun setupbarchart(dummynumber: Int) {
        if (dummynumber == 1) {
            val barangList = listOf("Susu", "Roti", "Kopi")
            val jumlahTerjualList = listOf(12f, 8f, 5f)

// Bikin BarEntry
            val entries = ArrayList<BarEntry>()
            for (i in barangList.indices) {
                entries.add(BarEntry(i.toFloat(), jumlahTerjualList[i]))
            }

// Dataset dan Data
            val dataSet = BarDataSet(entries, "Barang Terjual")
            dataSet.color = Color.parseColor("#3F51B5")
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 14f

            val barData = BarData(dataSet)
            barData.barWidth = 0.5f
            barChart.data = barData

// Styling BarChart
            barChart.setBackgroundColor(Color.WHITE)
            barChart.setDrawGridBackground(false)
            barChart.description.isEnabled = false
            barChart.axisRight.isEnabled = false

            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisLeft.textColor = Color.BLACK
            barChart.axisLeft.textSize = 12f

// XAxis pakai ValueFormatter
            val xAxis = barChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.textColor = Color.BLACK
            xAxis.textSize = 12f
            xAxis.granularity = 1f
            xAxis.setCenterAxisLabels(false)
            xAxis.valueFormatter = IndexAxisValueFormatter(barangList) // 🔥 ini buat nampilin nama barang

// Legend
            barChart.legend.isEnabled = false

// Animasi
            barChart.animateY(1000)

// Refresh chart
            barChart.invalidate()


        } else {
        }
    }

    private fun inisialisasi(view: View) {
        barChart = view.findViewById(R.id.barChart)
    }

    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        window.decorView.systemUiVisibility = 0
    }
}