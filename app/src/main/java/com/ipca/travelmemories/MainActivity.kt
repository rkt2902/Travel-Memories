package com.ipca.travelmemories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ipca.travelmemories.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment_activity_main)

        // definir itens do menu inferior
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_navigationFooter_home,
                R.id.fragment_navigationFooter_profile
            )
        )

        // definir menu superior
        findViewById<Toolbar>(R.id.topToolbar)
            .setupWithNavController(navController, appBarConfiguration)

        // definir menu inferior
        val navView: BottomNavigationView = binding.bottomNavigationView
        navView.setupWithNavController(navController)
    }
}