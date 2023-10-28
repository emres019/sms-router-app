package com.github.emresarincioglu.smsrouter

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.github.emresarincioglu.smsrouter.core.designsystem.BottomNavBarOwner
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavBarOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
    }

    private fun setupViews() {

        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        bottomNavBar.setupWithNavController(navHost.navController)
    }

    override fun setBottomNavBarVisibility(visibility: Int, transition: Transition?) {

        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        if (visibility == bottomNavBar.visibility) {
            return
        }

        transition?.let {
            val llMain: ViewGroup = findViewById(R.id.ll_main)
            TransitionManager.beginDelayedTransition(llMain, it)
        }

        bottomNavBar.visibility = visibility
    }
}