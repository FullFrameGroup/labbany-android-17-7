package com.labbany.labbany.ui.auth.reset_password

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
import com.labbany.labbany.databinding.FragmentResetPasswordBinding
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.Utils
import org.koin.android.ext.android.inject

class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private val viewModel: RestPasswordViewModel by inject()
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = requireContext()

        binding.tb.tv.text = getString(R.string.reset_password)

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.tvSave.setOnClickListener {

            if (!verify()) return@setOnClickListener

            callApi()

        }

    }

    private fun callApi() {

        viewModel.resetPassword(
            requireArguments().getString(Constants.PHONE, ""),
            binding.etNewPassword.text.toString()
        )

        lifecycleScope.launchWhenStarted {
            viewModel.resetPasswordStateFlow.collect {

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
                        val response = it.response as GeneralResponse

                        visProgress(false)

                        when {
                            response.success -> {
                              //  Log.e(TAG, "result: done $response")

                                HelperDialog(
                                    getString(R.string.succ_reset_password),
                                    object : DialogsListener {
                                        override fun onDismiss() {
                                            navController.navigate(R.id.loginFragment)
                                        }
                                    }).show(parentFragmentManager, "")
                            }
                            response.code == Constants.Auth.PASSWORD_CODE -> {
                                binding.etNewPassword.error =
                                    getString(R.string.password_should_be_g_then_8)
                                binding.etConfirmPassword.error =
                                    getString(R.string.password_should_be_g_then_8)

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun verify(): Boolean {

        return Utils.validatePasswordET(
            binding.etNewPassword,
            getString(R.string.password_should_be_g_then_8)
        ) &&
                Utils.validatePasswordET(
                    binding.etConfirmPassword,
                    getString(R.string.password_should_be_g_then_8)
                ) &&
                Utils.validateMatchPassword(
                    binding.etNewPassword,
                    binding.etConfirmPassword,
                    getString(R.string.password_not_matcher)
                )
    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvSave.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvSave.visibility = View.VISIBLE
        }

    }


    companion object {
        private val TAG = this::class.java.name
    }

}