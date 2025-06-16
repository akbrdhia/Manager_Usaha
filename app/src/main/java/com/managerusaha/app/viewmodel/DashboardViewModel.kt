package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.managerusaha.app.repository.BarangRepository
import com.managerusaha.app.utills.model.TopBarang
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
}
