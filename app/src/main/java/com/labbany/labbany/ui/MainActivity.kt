package com.labbany.labbany.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationBarView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ActivityMainBinding
import com.labbany.labbany.util.Constants

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    val basketLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val orderId =
                    result.data!!.extras!!.getSerializable(Constants.ORDER_ID) as Int

                val bundle = Bundle()
                bundle.putInt(Constants.ORDER_ID, orderId)

                navController.navigate(R.id.orderDetailsFragment, bundle)


            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        navController.addOnDestinationChangedListener(this)
        binding.bottomNavigation.setOnItemSelectedListener(this)
//        binding.bottomNavigation.selectedItemId = R.id.home_page

    }

    @SuppressLint("RestrictedApi")
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        when (destination.id) {

            R.id.homeFragment -> {

                if (navController.currentDestination!!.id == R.id.homeFragment)
                    binding.bottomNavigation.selectedItemId = R.id.home_page

                visMainView(true)
            }
            R.id.profileFragment -> {

                if (navController.currentDestination!!.id == R.id.profileFragment)
                    binding.bottomNavigation.selectedItemId = R.id.user_page

                visMainView(true)
            }
            R.id.walletFragment -> {

                if (navController.currentDestination!!.id == R.id.walletFragment)
                    binding.bottomNavigation.selectedItemId = R.id.wallet_page

                visMainView(true)
            }
            R.id.notificationFragment -> {

                if (navController.currentDestination!!.id == R.id.notificationFragment)
                    binding.bottomNavigation.selectedItemId = R.id.notifications_page

                visMainView(true)
            }
            R.id.productDetailsFragment -> {
                visMainView(true)
            }
            /*  R.id.citiesDialog -> {
                  visMainView(true)
              }
            */  else -> {
            visMainView(false)
        }

        }

    }

    private fun visMainView(b: Boolean) {

        if (b) {
            binding.bottomNavigation.visibility = VISIBLE
//            binding.tb.root.visibility = VISIBLE

        } else {
            binding.bottomNavigation.visibility = GONE
//            binding.tb.root.visibility = GONE

        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.user_page -> {
            if (navController.currentDestination!!.id != R.id.profileFragment)
                navController.navigate(R.id.profileFragment)

            true
        }
        R.id.home_page -> {
            if (navController.currentDestination!!.id != R.id.homeFragment)
                navController.navigate(R.id.homeFragment)

            true
        }
        R.id.notifications_page -> {
            if (navController.currentDestination!!.id != R.id.notificationFragment)
                navController.navigate(R.id.notificationFragment)

            true
        }
        R.id.wallet_page -> {
            if (navController.currentDestination!!.id != R.id.walletFragment)
                navController.navigate(R.id.walletFragment)

            true
        }
        else -> {
            navController.navigate(R.id.homeFragment)
            true
        }
    }

    /*   override fun onBackPressed() {
           super.onBackPressed()
           if (navController.currentDestination!!.id == R.id.splashFragment)
               finish()
         *//*  else if (navController.currentDestination!!.id == R.id.loginFragment)
            finish()*//*
    }*/

    companion object {
//        private val TAG = this::class.java.name
    }
}