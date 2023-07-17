package com.labbany.labbany.ui.loading

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import com.labbany.labbany.R
import com.labbany.labbany.databinding.DialogLoadingBinding


class LoaderDialog(mContext: Context) : Dialog(mContext) {

    lateinit var binding: DialogLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUp()

        setContentView(R.layout.dialog_loading)
    }

    private fun setUp() {


        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val wlp: WindowManager.LayoutParams = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window!!.attributes = wlp

        window?.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }

}