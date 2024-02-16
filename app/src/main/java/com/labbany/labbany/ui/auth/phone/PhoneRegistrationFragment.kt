package com.labbany.labbany.ui.auth.phone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentPhoneRegisterationBinding
import com.labbany.labbany.pojo.response.PhoneRegistrationResponse
import com.labbany.labbany.ui.auth.forget_password.CountriesAdapter
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import org.koin.android.ext.android.inject


class PhoneRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentPhoneRegisterationBinding
    private lateinit var navController: NavController
    private val viewModel: PhoneRegistrationViewModel by inject()
    private lateinit var mContext: Context
    private lateinit var adapter: CountriesAdapter
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_phone_registeration,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(binding.root)
        mContext = requireContext()
        adapter = CountriesAdapter()
        userId = requireArguments().getInt(Constants.USER_ID)

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.spCountries.adapter = adapter

        adapter.submitData(viewModel.counties())

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.tvNext.setOnClickListener {


            if (!verify()) return@setOnClickListener

            phoneRegistration()

        }

    }

    private fun phoneRegistration() {

        viewModel.phoneRegistration(
            userId, binding.etPhone.text.toString(),
        )

        lifecycleScope.launchWhenStarted {
            viewModel.phoneRegistrationStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(true)
                    }
                    is NetworkState.Error -> {
                        visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as PhoneRegistrationResponse

                        visProgress(false)

                        when {
                            response.success -> {
                              //  Log.e(TAG, "result: done $response")

                                val bundle = Bundle()

                                bundle.putInt(Constants.USER_ID, userId)
                                bundle.putString(
                                    Constants.PHONE,
                                    binding.etPhone.text.toString()
                                )

                                if (requireArguments().containsKey(Constants.ACTION))
                                    if (requireArguments().getString(Constants.ACTION) == Constants.FORGET_PASSWORD)
                                        bundle.putString(
                                            Constants.ACTION,
                                            Constants.FORGET_PASSWORD
                                        )


                                navController.navigate(R.id.otpFragment, bundle)

                            }
                            response.code == Constants.Auth.PHONE_CODE -> {
                                binding.etPhone.error = getString(R.string.phone_exist)
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }


    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvNext.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvNext.visibility = View.VISIBLE
        }

    }

    private fun verify(): Boolean {

        return Utils.validateET(binding.etPhone, null) &&
                Utils.validatePhoneET(binding.etPhone, getString(R.string.phone_validate))
    }

    companion object {
        private val TAG = this::class.java.name
    }

}