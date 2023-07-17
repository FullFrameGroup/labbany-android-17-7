package com.labbany.labbany.ui.my_city

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.DialogLocationBinding
import com.labbany.labbany.pojo.model.CityModel
import com.labbany.labbany.pojo.response.CitiesResponse
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class CitiesDialog(private val dialogsListener: DialogsListener?,private val sDialogsListener: DialogsListener?=null): DialogFragment() {

    private lateinit var binding: DialogLocationBinding
    lateinit var adapter: CitiesAdapter
    private val viewModel: CitiesViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_location, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()

        adapter = CitiesAdapter()
        mContext=requireContext()

        binding.spCities.adapter = adapter

        cities()

        binding.tvDone.setOnClickListener {
            setCity()
        }

    }

    private fun cities() {

        viewModel.cities()

        lifecycleScope.launchWhenStarted {
            viewModel.citiesStateFlow.collect {

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
                        val response = it.response as CitiesResponse

                        visProgress( false)

                        when {
                            response.success -> {

                                if (!response.data.cities.isNullOrEmpty()) {

                                    for (i in response.data.cities.indices) {

                                        val item = response.data.cities[i]

                                        adapter.submitData(item)

                                        if (shardHelper.cityId == item.city_id)
                                            binding.spCities.setSelection(i)

                                    }

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

    private fun auth(): Boolean = shardHelper.isLogged()

    private fun setCity() {

        if (!verify()) return

        val selectedCity: CityModel = adapter.getItem(binding.spCities.selectedItemPosition)

        if (!auth()){

            doAction(selectedCity)

            return
        }

        if (shardHelper.cityId == selectedCity.city_id) {

//            findNavController().popBackStack()
            dismiss()
            return
        }


        viewModel.setCity(
            shardHelper.id, selectedCity.city_id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.setCityStateFlow.collect {

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
                        val response = it.response as GeneralResponse

                        visProgress( false)

                        when {
                            response.success -> {
                                doAction(selectedCity)
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun doAction(selectedCity: CityModel) {
        shardHelper.setCityData(selectedCity)

        dialogsListener?.onDismiss(selectedCity)

        sDialogsListener?.onDismiss(selectedCity)

        Utils.toast(requireContext(),getString(R.string.city_updated))

        dismiss()
    }

    private fun setUp() {
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable=true
    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvDone.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvDone.visibility = View.VISIBLE
        }

    }

    private fun verify(): Boolean {

        val b: Boolean = binding.spCities.selectedItemPosition != -1
        if (!b)
            Utils.toast(requireContext(), getString(R.string.should_selecet_city))

        return b
    }

    companion object {
//        private val TAG = this::class.java.name
    }

}