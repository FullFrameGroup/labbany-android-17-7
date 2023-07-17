package com.labbany.labbany.ui.complaints

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
import com.labbany.labbany.databinding.FragmentComplaintsBinding
import com.labbany.labbany.pojo.response.ComplaintsResponse
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.labbany.labbany.util.ShardHelper
import org.koin.android.ext.android.inject

class ComplaintsFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentComplaintsBinding
    private val viewModel: ComplaintsViewModel by inject()
    private lateinit var adapter: ComplaintsAdapter
    private val shardHelper: ShardHelper by inject()
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complaints, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tb.tvTitle.text = getString(R.string.upload_problem)

        navController = Navigation.findNavController(binding.root)
        mContext = requireContext()

        adapter = ComplaintsAdapter()

        binding.rvComplaints.adapter = adapter

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.tvUpload.setOnClickListener {
            navController.navigate(R.id.uploadProblemFragment)
        }

        complaints()

        setUp()

    }

    private fun setUp() {

        navController.addOnDestinationChangedListener { _, _, _ ->
            val arg = arguments?.keySet()?.toMutableList()

            Log.e(TAG, "setUp: $arg")

            complaints()
        }

    }

    private fun complaints() {

        viewModel.complaints(
            shardHelper.id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.complaintsStateFlow.collect {

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
                        val response = it.response as ComplaintsResponse

                        visProgress(false)

                        when {
                            response.success -> {

                                if (response.data.isNullOrEmpty()) {
                                    binding.tvEmptyComplaints.visibility = View.VISIBLE
                                } else {
                                    binding.tvEmptyComplaints.visibility = View.GONE
                                    adapter.submitData(response.data)

                                }

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
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }

}