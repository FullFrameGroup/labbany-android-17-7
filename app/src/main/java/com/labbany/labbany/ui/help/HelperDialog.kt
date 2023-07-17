package com.labbany.labbany.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.labbany.labbany.R
import com.labbany.labbany.databinding.DialogHelpBinding
import com.labbany.labbany.util.DialogsListener

class HelperDialog(
    private val msg: String,
    private val mDialogsListener: DialogsListener?
) : DialogFragment() {

    private lateinit var binding: DialogHelpBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_help,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable=false

        binding.tvMsg.text = msg

        binding.tvOk.setOnClickListener {
            mDialogsListener?.onDismiss()
            dismiss()
        }

    }


}