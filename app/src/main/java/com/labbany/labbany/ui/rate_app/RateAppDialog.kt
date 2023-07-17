package com.labbany.labbany.ui.rate_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.databinding.DialogRateAppBinding

class RateAppDialog : DialogFragment() {

    private lateinit var binding: DialogRateAppBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.dialog_rate_app, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        navController = findNavController()

        binding.tvRate.setOnClickListener {
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

    }

    private fun setUp() {

        dialog!!.  window!!.setBackgroundDrawableResource(android.R.color.transparent)

    }

}