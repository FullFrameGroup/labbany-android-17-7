package com.labbany.labbany.ui.offer_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.databinding.DialogOfferBinding
import com.labbany.labbany.util.Constants
import com.squareup.picasso.Picasso


class OfferDetailsDialog : DialogFragment() {

    private lateinit var binding: DialogOfferBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_offer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        navController = findNavController()

        if (arguments != null &&
            requireArguments().containsKey(Constants.IMAGE) &&
            !requireArguments().getString(Constants.IMAGE, "").isNullOrEmpty()
        )
            Picasso.get().load(requireArguments().getString(Constants.IMAGE, ""))
                .into(binding.img)

        binding.imgClose.setOnClickListener {
            dismiss()
        }

    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

    }

}