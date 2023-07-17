package com.labbany.labbany.ui.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import com.labbany.labbany.databinding.FragmentWalletBinding
import com.labbany.labbany.pojo.response.WalletResponse
import com.labbany.labbany.ui.MainActivity
import com.labbany.labbany.ui.views.MainToolbar
import com.labbany.labbany.util.ShardHelper
import org.koin.android.ext.android.inject

class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var transactionsAdapter: TransactionsAdapter
    private val viewModel: WalletViewModel by inject()
    private lateinit var navController: NavController
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireView())
        transactionsAdapter = TransactionsAdapter()
        mContext = requireContext()

        if (!auth()) return

        binding.rvTransactions.adapter = transactionsAdapter

        data()

        MainToolbar(
            binding.tb,
            mContext,
            null,
            (requireActivity() as MainActivity).basketLauncher
        )


    }

    private fun auth(): Boolean = if (shardHelper.isLogged())
        true
    else {
        navController.navigate(R.id.notAuthDialog)
        false
    }

    @SuppressLint("SetTextI18n")
    private fun data() {

        viewModel.walletDetails(
            shardHelper.id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.walletStateFlow.collect {

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
                        val response = it.response as WalletResponse

                        visProgress(false)

                        when {
                            response.success -> {

                                if (response.data == null || response.data.wallet_list.isNullOrEmpty()) {
                                    visLabel(true)
                                } else {
                                    visLabel(false)
                                    transactionsAdapter.submitData(response.data.wallet_list as ArrayList<WalletResponse.Wallet>)
                                }

                                binding.tvWallet.text =
                                    "${response.data!!.wallet_amount} ${getString(R.string.sar)}"

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun visProgress(progressState: Boolean) {

        if (progressState) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
        }

    }

    private fun visLabel(state: Boolean) {

        if (state) {
            binding.tvEmptyWallet.visibility = View.VISIBLE
        } else {
            binding.tvEmptyWallet.visibility = View.GONE
        }

    }


}