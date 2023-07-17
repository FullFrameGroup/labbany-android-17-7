package com.labbany.labbany.ui.auth.otp.coupon_for_new_user

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.databinding.DialogNewUserBinding
import com.labbany.labbany.util.*

class NewUserDialog : DialogFragment() {

    private lateinit var binding: DialogNewUserBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_new_user, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        navController = findNavController()
        mContext = requireContext()

        binding.tvMsg.text = requireArguments().getString(Constants.COUPON_MESSAGE) ?: ""

        binding.tvOk.setOnClickListener {

//            navController.navigate(R.id.homeFragment)
            dismiss()

        }

        binding.imgCopy.setOnClickListener {

            copy(requireArguments().getString(Constants.COUPON_CODE) ?: "")
        }


    }

    private fun copy(code: String) {

        Utils.copyTextToClipBoard(mContext, code)

        Toast.makeText(mContext, "تم نسخ الكود بنجاح", Toast.LENGTH_SHORT).show()

    }

    private fun setUp() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            })

    }


    companion object {
//        private val TAG = this::class.java.name
    }

}