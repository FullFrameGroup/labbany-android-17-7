package com.labbany.labbany.ui.add_address

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentAddAddressBinding
import com.labbany.labbany.pojo.model.SimpleAddressModel
import com.labbany.labbany.pojo.model.SimpleLocationModel
import com.labbany.labbany.pojo.response.AddAddressResponse
import com.labbany.labbany.pojo.response.CitiesResponse
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.addresses.AddressViewModel
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.ui.select_location.SelectLocationActivity
import com.labbany.labbany.ui.views.SharedViewModel
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.Serializable

class AddAddressFragment : Fragment() {

    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var navController: NavController
    private val viewModel: AddressViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private val sharedViewModel by sharedViewModel<SharedViewModel>()
    private var simpleLocationModel: SimpleLocationModel? = null
    private var simpleAddressModel: SimpleAddressModel? = null
    private lateinit var citiesAdapter2: CitiesAdapter2
    private lateinit var countriesAdapter2: CountriesAdapter2
    private lateinit var mContext: Context
    private lateinit var mFragmentManager: FragmentManager

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data =
                    result.data!!.extras!!.getSerializable(Constants.DATA) as SimpleLocationModel

                simpleLocationModel = data

                binding.tvAddress.text = simpleLocationModel!!.location
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = requireContext()
        mFragmentManager = parentFragmentManager

        citiesAdapter2 = CitiesAdapter2()
        countriesAdapter2 = CountriesAdapter2()

        binding.spCities.adapter = citiesAdapter2
        binding.spCountries.adapter = countriesAdapter2

        countriesAdapter2.submitData(viewModel.counties(requireContext()))

        cities()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.tvAddress.setOnClickListener { startLocationActivity() }

        binding.tvAddAddress.setOnClickListener {

            if (!verify()) return@setOnClickListener

            if (isUpdate())
                updateAddress()
            else
                addAddress()
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
                        visProgress(true)
                    }
                    is NetworkState.Error -> {
                        visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {

                        visProgress(false)
                        val response = it.response as CitiesResponse


                        when {
                            response.success -> {

                                if (!response.data.cities.isNullOrEmpty()) {

                                    fullUIIfUpdateMode()

                                    for (i in response.data.cities.indices) {

                                        val item = response.data.cities[i]

                                        citiesAdapter2.submitData(item)

                                        if (isUpdate()) {
                                            if (simpleAddressModel != null && simpleAddressModel!!.city_id == item.city_id)
                                                binding.spCities.setSelection(i)

                                        } else {
                                            if (shardHelper.cityId == item.city_id)
                                                binding.spCities.setSelection(i)
                                        }
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

    private fun fullUIIfUpdateMode() {

        if (isUpdate()) {

            simpleAddressModel =
                requireArguments().getSerializable(Constants.DATA) as SimpleAddressModel

            simpleLocationModel = SimpleLocationModel(
                simpleAddressModel!!.district_name,
                simpleAddressModel!!.latitude!!,
                simpleAddressModel!!.longitude!!,
                simpleAddressModel!!.location
            )

            binding.etDistrict.setText(simpleAddressModel!!.district_name)
            binding.etReceiverPhone.setText(simpleAddressModel!!.alter_contact)
            binding.etReceiverName.setText(simpleAddressModel!!.alter_name)

            binding.tvAddress.text = simpleAddressModel!!.location

        }

    }

    private fun startLocationActivity() {

        val intent = Intent(mContext, SelectLocationActivity::class.java)

        if (isUpdate())
            intent.putExtra(Constants.DATA, simpleLocationModel as Serializable)

        resultLauncher.launch(intent)

    }

    private fun addAddress() {

        viewModel.addAddress(
            shardHelper.id,
            simpleLocationModel!!.location,
            binding.etDistrict.text.toString(),
            citiesAdapter2.getItem(binding.spCities.selectedItemPosition).city_id,
            binding.etReceiverName.text.toString(),
            binding.etReceiverPhone.text.toString(),
            simpleLocationModel!!.latitude,
            simpleLocationModel!!.longitude
        )

        lifecycleScope.launchWhenStarted {
            viewModel.addAddressStateFlow.collect {

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

                        visProgress(false)
                        val response = it.response as AddAddressResponse


                        when {
                            response.success -> {

                                val dialogsListener = object : DialogsListener {
                                    override fun onDismiss() {
                                        sharedViewModel.addressUpdateState.value = true
                                        navController.popBackStack()
                                    }
                                }

                                HelperDialog(
                                    getString(R.string.address_added),
                                    dialogsListener
                                ).show(
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

    private fun updateAddress() {

        viewModel.updateAddress(
            shardHelper.id, simpleAddressModel!!.shipping_id,
            simpleLocationModel!!.location,
            binding.etDistrict.text.toString(),
            citiesAdapter2.getItem(binding.spCities.selectedItemPosition).city_id,
            binding.etReceiverName.text.toString(),
            binding.etReceiverPhone.text.toString(),
            simpleLocationModel!!.latitude,
            simpleLocationModel!!.longitude
        )

        lifecycleScope.launchWhenStarted {
            viewModel.updateAddressStateFlow.collect {

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

                        visProgress(false)
                        val response = it.response as GeneralResponse


                        when {
                            response.success -> {

                                val dialogsListener = object : DialogsListener {
                                    override fun onDismiss() {
                                        sharedViewModel.addressUpdateState.value = true
                                        navController.popBackStack()
                                    }
                                }

                                HelperDialog(
                                    getString(R.string.address_updated),
                                    dialogsListener
                                ).show(
                                    parentFragmentManager,
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

    private fun isUpdate(): Boolean =
        arguments != null && requireArguments().containsKey(Constants.UPDATE_ADDRESS)
                && requireArguments().getBoolean(Constants.UPDATE_ADDRESS)

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvAddAddress.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvAddAddress.visibility = View.VISIBLE
        }

    }

    private fun verify(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etDistrict, null)) isNull = true
        if (!Utils.validateET(binding.etReceiverPhone, null)) isNull = true
        if (!Utils.validateET(binding.etReceiverName, null)) isNull = true
        if (simpleLocationModel == null) {
            HelperDialog(getString(R.string.select_location_first), null).show(
                parentFragmentManager,
                ""
            )
            isNull = true
        }

        if (binding.spCities.selectedItemPosition == -1) {
            Utils.toast(requireContext(), getString(R.string.should_selecet_city))
            isNull = true
        }

      //  Log.e(TAG, "verify: isNull $isNull")

        return !isNull
    }

    companion object {
        private val TAG = this::class.java.name
    }

}