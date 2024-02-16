package com.labbany.labbany.ui.payment

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
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentPaymentMethodsBinding
import com.labbany.labbany.pojo.model.VisaModel
import com.labbany.labbany.pojo.response.AllVisasResponse
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.add_visa.VisasViewModel
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class PaymentMethodsFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentPaymentMethodsBinding
    private lateinit var adapter: PaymentAdapter
    private val viewModel: VisasViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_payment_methods, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        mContext = requireContext()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        adapter = PaymentAdapter(this)

        binding.rvTransactions.adapter = adapter

        data()

    }


    private fun data() {

        viewModel.allVisas(
            shardHelper.id,
            null,
        )

        lifecycleScope.launchWhenStarted {
            viewModel.allVisasStateFlow.collect {

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
//                      //  Log.e(TAG, "${it.response}")

                        visProgress(false)
                        val response = it.response as AllVisasResponse

//                      //  Log.e(TAG, "result: response $response")

                        visProgress(progressState = false)

                        when {
                            response.success -> {

                                if (response.data.isNullOrEmpty())
                                    noVisas()
                                else
                                    adapter.submitData(response.data as ArrayList<VisaModel>)

                            }
                            response.code == Constants.Visas.VISA_CODE -> {
                                noVisas()
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
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }

    }

    private fun noVisas() {
        HelperDialog(getString(R.string.no_visas), null).show(
            parentFragmentManager,
            ""
        )
    }

    override fun onRemoveVisa(
        visaModel: VisaModel,
        viewHolder: PaymentAdapter.ViewHolder,
        position: Int
    ) {

        viewModel.deleteVisa(
            shardHelper.id,
            visaModel.visa_id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.deleteVisaStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        viewHolder.visProgress(progressState = true)
                    }
                    is NetworkState.Error -> {
                        viewHolder.visProgress(progressState = false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as GeneralResponse

//                      //  Log.e(TAG, "result: response $response")

                        viewHolder.visProgress(progressState = false)

                        when {
                            response.success -> {

                                Utils.toast(requireContext(), getString(R.string.card_removed))
                                adapter.removeVisa(visaModel)
                            }
                            response.code == Constants.Visas.VISA_CODE -> {
                                HelperDialog(getString(R.string.no_visas), null).show(
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

    companion object {
        private val TAG = this::class.java.name
    }

}