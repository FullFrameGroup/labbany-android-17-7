package com.labbany.labbany.ui.addresses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ActivityAddressesBinding

class AddressesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressesBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_addresses)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

    }

    override fun onBackPressed() {

        if (navController.currentDestination!!.id == R.id.addressFragment)
            finish()
        else
            super.onBackPressed()

    }
}