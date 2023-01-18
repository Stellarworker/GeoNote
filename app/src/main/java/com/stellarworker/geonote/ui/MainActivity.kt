package com.stellarworker.geonote.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.stellarworker.geonote.R
import com.stellarworker.geonote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_activity_fragment_container) as NavHostFragment
        setupActionBarWithNavController(
            navHostFragment.navController,
            AppBarConfiguration(
                setOf(
                    R.id.navigation_map,
                    R.id.navigation_markers,
                )
            )
        )
        binding.mainActivityBottomNavigation
            .setupWithNavController(navHostFragment.navController)
    }

}