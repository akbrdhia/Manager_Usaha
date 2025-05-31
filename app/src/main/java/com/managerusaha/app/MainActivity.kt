package com.managerusaha.app

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.managerusaha.app.fragment.HomeFragment
import com.managerusaha.app.fragment.LainnyaFragment
import com.managerusaha.app.fragment.RiwayatFragment
import com.managerusaha.app.fragment.StokFragment
import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private var currentFragmentTag: String? = null

    // Variabel buat logika back‐button
    private var backPressedTime: Long = 0
    private val backPressThreshold = 2000L // 2 detik

    // Simpan reference callback supaya bisa di‐enable lagi di onStart()
    private lateinit var backCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)
        setDefault()
        backPressedTime = 0
        Log.d("BACKDEBUG", "backPressedTime di-reset di onCreate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        // Setup BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment(), "HOME")
        }
        bottomNav.setOnItemSelectedListener { item ->
            val (fragment, tag) = when (item.itemId) {
                R.id.nav_home -> HomeFragment() to "HOME"
                R.id.nav_stok -> StokFragment() to "STOK"
                R.id.nav_riwayat -> RiwayatFragment() to "RIWAYAT"
                R.id.nav_lainnya -> LainnyaFragment() to "LAINNYA"
                else -> return@setOnItemSelectedListener false
            }
            if (tag != currentFragmentTag) {
                replaceFragment(fragment, tag)
            }
            true
        }

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                Log.d("BACKDEBUG", "Selisih waktu = ${currentTime - backPressedTime}")
                if (currentTime - backPressedTime < backPressThreshold) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(this@MainActivity, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
                    backPressedTime = currentTime
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        backPressedTime = 0
        Log.d("BACKDEBUG", "backPressedTime di-reset di onNewIntent")
        backCallback.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
        backPressedTime = 0
        Log.d("BACKDEBUG", "backPressedTime di-reset di onStart")
        backCallback.isEnabled = true
    }

    // Ganti fragment di container
    fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
        currentFragmentTag = tag
    }

    private fun setDefault() {
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
    }
}
