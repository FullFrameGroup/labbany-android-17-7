package com.labbany.labbany.ui.visa_details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentVisaDetailsBinding
import com.labbany.labbany.pojo.model.VisaModel
import com.labbany.labbany.pojo.response.AllVisasResponse
import com.labbany.labbany.ui.add_visa.VisasViewModel
import com.labbany.labbany.ui.basket.payment.OnVisaSelected
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.ui.views.SharedViewModel
import com.labbany.labbany.util.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VisaDetailsFragment : DialogFragment() {

    private lateinit var binding: FragmentVisaDetailsBinding
    private lateinit var navController: NavController
    private val viewModel: VisasViewModel by inject()
    private lateinit var adapter: VisaAdapter
    private var visaTypeImage: String? = null
    private val shardHelper: ShardHelper by inject()
    private val sharedViewModel by sharedViewModel<SharedViewModel>()
    private lateinit var mContext: Context
    private lateinit var mFragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_visa_details, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onVisaSelected =
/*requireArguments().getSerializable(Constants.ON_VISA_SELECTED) as OnVisaSelected as*/
            object : OnVisaSelected {
                override fun onVisaSelected(visa: VisaModel) {
                    sharedViewModel.visaStateFlow.value = NetworkState.Result(visa)

                    dismiss()

                }
            }

        navController = findNavController()
        adapter = VisaAdapter(onVisaSelected)
        mContext = requireContext()
        mFragmentManager = parentFragmentManager
        setUp()

        if (requireArguments().containsKey(Constants.IMAGE)) {
            if (!requireArguments().getString(Constants.IMAGE, "").isNullOrEmpty()) {
                visaTypeImage = requireArguments().getString(Constants.IMAGE, "")
                Picasso.get().load(visaTypeImage).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        binding.img.setImageBitmap(bitmap)
                        adapter.setVisaTypeImage(bitmap)

                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })
            }
        }

        binding.tv1.text = requireArguments().getString(Constants.VISA_TYPE_NAME, "")

        binding.rvVisa.adapter = adapter

        data()

        listenVisaAdded()

        binding.tvAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(
                Constants.VISA_TYPE_ID,
                requireArguments().getInt(Constants.VISA_TYPE_ID)
            )

            bundle.putString(
                Constants.VISA_TYPE_NAME,
                requireArguments().getString(Constants.VISA_TYPE_NAME, "")
            )

            if (!visaTypeImage.isNullOrEmpty())
                bundle.putString(Constants.IMAGE, visaTypeImage)

            navController.navigate(R.id.addVisaFragment, bundle)
        }

    }

    private fun listenVisaAdded() {

        lifecycleScope.launchWhenStarted {
            sharedViewModel.visaAddedStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {}
                    is NetworkState.Loading -> {}
                    is NetworkState.Error -> {}
                    is NetworkState.Result<*> -> {

                        data()
                        sharedViewModel.visaAddedStateFlow.value = NetworkState.Idle
                    }

                }
            }
        }

    }

    private fun data() {

        viewModel.allVisas(
            shardHelper.id,
            requireArguments().getInt(Constants.VISA_TYPE_ID)
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
                        val response = it.response as AllVisasResponse

                        visProgress(false)

                        when {
                            response.success -> {

                                adapter.submitData(response.data as ArrayList<VisaModel>)
                            }
                            response.code == Constants.Visas.VISA_CODE -> {
                                HelperDialog(mContext.getString(R.string.no_visas), null).show(
                                    mFragmentManager,
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

    private fun visProgress(progressState: Boolean) {

        if (progressState) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
/*
        navController.addOnDestinationChangedListener { _, _, _ ->

            data()
        }
*/

    }

    override fun onResume() {
        super.onResume()
        data()
    }

/*override fun onVisaClickListener(visaModel: VisaModel) {

    sharedViewModel.visaStateFlow.value = visaModel

    dismiss()

}*/
}