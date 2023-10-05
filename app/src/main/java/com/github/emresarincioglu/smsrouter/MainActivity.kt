package com.github.emresarincioglu.smsrouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.github.emresarincioglu.smsrouter.core.designsystem.R as designSystemR

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
    }

    private fun setupViews() {

        val bottomNavBar: BottomNavigationView = findViewById(designSystemR.id.bottom_nav_bar)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        bottomNavBar.setupWithNavController(navHost.navController)
    }
}