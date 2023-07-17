package com.labbany.labbany.ui.auth.otp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentOTPBinding
import com.labbany.labbany.pojo.response.CheckCodeResponse
import com.labbany.labbany.pojo.response.OtpResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private lateinit var navController: NavController
    private val viewModel: OtpViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private var userId: Int = 0
    private lateinit var phone: String
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_o_t_p, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(binding.root)
        userId = requireArguments().getInt(Constants.USER_ID)
        phone = requireArguments().getString(Constants.PHONE, "")
        mContext = requireContext()

        sendOtp()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.tvResend.setOnClickListener { sendOtp() }

        binding.tvConfirm.setOnClickListener {

            if (!verify())
                return@setOnClickListener

            if (requireArguments().containsKey(Constants.ACTION)) {
                if (requireArguments().getString(Constants.ACTION) == Constants.FORGET_PASSWORD) {
                    checkCode(Constants.FORGET_PASSWORD_PARAMS)
                }

            } else {
                checkCode(Constants.REGISTRATION_PARAMS)
            }
        }

    }

    private fun sendOtp() {

        viewModel.sendOrResendOtp(
            userId, phone
        )

        lifecycleScope.launchWhenStarted {
            viewModel.sendOrResendOtpStateFlow.collect {

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
                        val response = it.response as OtpResponse

                        visProgress(false)

                        when {
                            response.success -> {
                                Log.e(TAG, "result: done $response")

                                binding.otpView.setOTP(response.data.otp)
                            }
                            response.code == Constants.Auth.OTP_CODE -> {
                                HelperDialog(getString(R.string.known_error), null).show(
                                    parentFragmentManager,
                                    ""
                                )
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

    }

    private fun checkCode(is_registration: Int) {

        viewModel.codeCheck(
            userId, binding.otpView.otp!!, is_registration
        )

        lifecycleScope.launchWhenStarted {
            viewModel.codeCheckStateFlow.collect {

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
                        val response = it.response as CheckCodeResponse

                        visProgress(false)

                        when {
                            response.success -> {
                                Log.e(TAG, "result: done $response")

                                if (requireArguments().containsKey(Constants.ACTION)) {
                                    if (requireArguments().getString(Constants.ACTION) == Constants.FORGET_PASSWORD) {
                                        val bundle = Bundle()

                                        bundle.putString(Constants.PHONE, phone)
                                        navController.navigate(R.id.resetPasswordFragment, bundle)
                                    }

                                } else {
                                    shardHelper.saveData(response.data!!)

//                                    if (response.data.coupon.isNullOrEmpty()) {
//                                        navController.navigate(R.id.homeFragment)
//                                    } else {
                                    val bundle = Bundle()

                                    bundle.putString(
                                        Constants.COUPON_MESSAGE,
                                        response.data.coupon_msg
                                    )
                                    bundle.putString(
                                        Constants.COUPON_CODE,
                                        response.data.coupon
                                    )

                                    navController.navigate(R.id.homeFragment, bundle)
//                                    }

                                }
                            }
                            response.code == Constants.Auth.OTP_CODE -> {
                                HelperDialog(getString(R.string.otp_error), null).show(
                                    parentFragmentManager,
                                    ""
                                )
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
            binding.tvConfirm.visibility = View.INVISIBLE
            binding.tvResend.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvConfirm.visibility = View.VISIBLE
            binding.tvResend.visibility = View.VISIBLE
        }

    }

    private fun verify(): Boolean {

        return Utils.validateOtp(binding.otpView)
    }

    companion object {
        private val TAG = this::class.java.name
    }


}