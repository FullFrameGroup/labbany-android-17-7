package com.labbany.labbany.ui.auth.logout

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.DialogLogoutBinding
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.ShardHelper
import com.labbany.labbany.util.Utils
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.koin.android.ext.android.inject

class LogoutDialog(
    private val mDialogsListener: DialogsListener?
) : DialogFragment() {

    private lateinit var binding: DialogLogoutBinding
    private val viewModel: LogoutViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_logout,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mContext=requireContext()

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvLogout.setOnClickListener {
            logout()
        }

    }

    private fun logout() {

        try {
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
        }

        try {
            mGoogleSignInClient.signOut()
        } catch (e: Exception) {
        }

        viewModel.logout(
            shardHelper.id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.logoutStateFlow.collect {

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
                        val response = it.response as GeneralResponse

                        visProgress( false)

                        when {
                            response.success -> {
                                Log.e(TAG, "result: done $response")

                                shardHelper.logOut()

                                Utils.toast(requireContext(), getString(R.string.logout_succ))

                                mDialogsListener?.onDismiss()
                                dismiss()

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
            binding.tvLogout.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvLogout.visibility = View.VISIBLE
        }

    }


    companion object {
        private val TAG = this::class.java.name
    }

}