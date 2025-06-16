package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.managerusaha.app.repository.BarangRepository
import com.managerusaha.app.utills.model.TopBarang
import com.managerusaha.app.utills.model.TopBarang3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = BarangRepository(application)

    // LiveData top 6 barang terlaris bulan ini
    private val _top6 = MutableLiveData<List<TopBarang>>()
    val top6: LiveData<List<TopBarang>> = _top6

    fun loadTop6BulanIni() {
        viewModelScope.launch(Dispatchers.IO) {
            val bulanIni = getStartOfMonthMillis()
            // anggap repo.getTop6(bulanIni) mengembalikan List<TopBarang>
            val data = repo.getTop6TerlarisSejak(bulanIni)
            _top6.postValue(data)
        }
    }

    private fun getStartOfMonthMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private val _top3 = MutableLiveData<List<TopBarang3>>()
    val top3: LiveData<List<TopBarang3>> = _top3

    fun loadTop3(startMillis: Long) {
        viewModelScope.launch {
            val data = repo.getTopTerlarisSejak(startMillis, 3)
            _top3.postValue(data)
        }
    }

    fun startOfToday(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun startOfWeek(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek) // biasanya Minggu atau Senin tergantung lokal
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun startOfMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun startOfTomorrow(): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis



    private fun startOfNextWeek(): Long = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_YEAR, 1)
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis



    private fun startOfNextMonth(): Long = Calendar.getInstance().apply {
        add(Calendar.MONTH, 1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis


    private val _pendapatanHari = MutableLiveData<Double>()
    val pendapatanHari: LiveData<Double> = _pendapatanHari

    private val _pendapatanMinggu = MutableLiveData<Double>()
    val pendapatanMinggu: LiveData<Double> = _pendapatanMinggu

    private val _pendapatanBulan = MutableLiveData<Double>()
    val pendapatanBulan: LiveData<Double> = _pendapatanBulan

    fun loadPendapatan() {
        viewModelScope.launch {
            val t0 = startOfToday()
            val t1 = startOfTomorrow()
            _pendapatanHari.postValue(repo.getPendapatan(t0, t1))

            val w0 = startOfWeek()
            val w1 = startOfNextWeek()
            _pendapatanMinggu.postValue(repo.getPendapatan(w0, w1))

            val m0 = startOfMonth()
            val m1 = startOfNextMonth()
            _pendapatanBulan.postValue(repo.getPendapatan(m0, m1))
        }
    }

}
