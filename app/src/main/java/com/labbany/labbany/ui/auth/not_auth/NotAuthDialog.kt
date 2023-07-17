package com.labbany.labbany.ui.auth.not_auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.databinding.DialogAuthBinding

class NotAuthDialog : DialogFragment() {

    private lateinit var binding: DialogAuthBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_auth, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        navController = findNavController()

        binding.tvLogin.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        binding.tvCancel.setOnClickListener { dismiss() }

    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false

        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }
}