package com.example.managerusaha

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)
        setDefault()

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
    }









    //  method //

    private fun replaceFragment(fragment: Fragment, tag: String) {
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

