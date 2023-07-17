package com.labbany.labbany.ui.splash

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.databinding.FragmentSplashBinding
import com.labbany.labbany.util.ShardHelper
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class SplashFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSplashBinding
    private val shardHelper: ShardHelper by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(binding.root)

        isLogged()

        onBack()

        binding.tvLogin.setOnClickListener { navController.navigate(R.id.loginFragment) }

        binding.tvSignUp.setOnClickListener { navController.navigate(R.id.signUpFragment) }

        binding.tvNotAuth.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }

    }

    private fun isLogged() {

        visViews(false)

        if (shardHelper.isLogged()) {

            CoroutineScope(Dispatchers.IO).launch {

                delay(3000L)
                withContext(Dispatchers.Main){
                    navController.navigate(R.id.homeFragment)
                }

            }


        } else {
            visViews(true)
        }

    }

    private fun visViews(s:Boolean){

        if (s){
            binding.tvSignUp.visibility=View.VISIBLE
            binding.tvLogin.visibility=View.VISIBLE
            binding.tvNotAuth.visibility=View.VISIBLE
        }
        else{
            binding.tvSignUp.visibility=View.INVISIBLE
            binding.tvLogin.visibility=View.INVISIBLE
            binding.tvNotAuth.visibility=View.INVISIBLE
        }

    }

    private fun onBack() {

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })

    }
}