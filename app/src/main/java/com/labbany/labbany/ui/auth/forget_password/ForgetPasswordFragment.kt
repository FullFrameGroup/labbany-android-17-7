package com.labbany.labbany.ui.auth.forget_password

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
import com.labbany.labbany.databinding.FragmentForgetPasswordBinding
import com.labbany.labbany.pojo.response.RegistrationResponse
import com.labbany.labbany.ui.auth.otp.OtpViewModel
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class ForgetPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgetPasswordBinding
    private val viewModel: OtpViewModel by inject()
    private lateinit var navController: NavController
    private lateinit var adapter: CountriesAdapter
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_forget_password, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        adapter= CountriesAdapter()
        mContext=requireContext()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.spCountries.adapter=adapter

        adapter.submitData(viewModel.counties())

        binding.tvNext.setOnClickListener {

            if (!verify()) return@setOnClickListener

            callApi()

        }

    }

    private fun callApi() {

        viewModel.forgePassword(
            binding.etPhone.text.toString()
        )

        lifecycleScope.launchWhenStarted {
            viewModel.forgePasswordStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress( true)
                    }
                    is NetworkState.Error -> {
                        visProgress( false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as RegistrationResponse

                        visProgress( false)

                        when {
                            response.success -> {
//                              //  Log.e(TAG, "result: done $response")

                                val bundle=Bundle()

                                bundle.putString(Constants.ACTION, Constants.FORGET_PASSWORD)
                                bundle.putString(Constants.PHONE, binding.etPhone.text.toString())
                                bundle.putInt(Constants.USER_ID,response.data.id)

                                navController.navigate(R.id.otpFragment,bundle)
                            }
                            response.code== Constants.Auth.PHONE_CODE->{
                                binding.etPhone.error=getString(R.string.error_phone)
                            }  else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

    }

    private fun verify(): Boolean {

        return Utils.validateET(binding.etPhone, null) &&
                Utils.validatePhoneET(binding.etPhone, getString(R.string.phone_validate))
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


    companion object {
        private val TAG = this::class.java.name
    }

}