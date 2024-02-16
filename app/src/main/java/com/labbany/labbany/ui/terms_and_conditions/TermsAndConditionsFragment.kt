package com.labbany.labbany.ui.terms_and_conditions

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
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
import com.labbany.labbany.databinding.FragmentTermsAndConditionsBinding
import com.labbany.labbany.pojo.response.TermsResponse
import org.koin.android.ext.android.inject

class TermsAndConditionsFragment : Fragment() {

    private lateinit var binding: FragmentTermsAndConditionsBinding
    private lateinit var navController: NavController
    private val viewModel: TermsViewModel by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_terms_and_conditions,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext=requireContext()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        terms()
    }

    private fun terms() {

        viewModel.terms()

        lifecycleScope.launchWhenStarted {
            viewModel.termsStateFlow.collect {

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
                        val response = it.response as TermsResponse

                        visProgress( false)

                        when {
                            response.success -> {

//                              //  Log.e(TAG, "result: done $response")

                                binding.tvTerms.text= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    Html.fromHtml(response.data.content, Html.FROM_HTML_MODE_COMPACT)
                                else
                                    Html.fromHtml(response.data.content)

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