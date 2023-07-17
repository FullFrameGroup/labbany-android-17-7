package com.labbany.labbany.ui.addresses

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentAddressBinding
import com.labbany.labbany.pojo.model.SimpleAddressModel
import com.labbany.labbany.pojo.response.AddressesResponse
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject
import java.io.Serializable

class AddressFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel: AddressViewModel by inject()
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var navController: NavController
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = findNavController()
        mContext = requireContext()

        addressAdapter = AddressAdapter(this)

        binding.rvAddress.adapter = addressAdapter

        setUp()

        callApi()

        binding.tvAddAddress.setOnClickListener {
            navController.navigate(R.id.addAddressFragment)
        }

        binding.tb.imgBack.setOnClickListener { requireActivity().finish() }

    }

    private fun callApi() {

        viewModel.address(
            shardHelper.id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.addressesStateFlow.collect {

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
                        val response = it.response as AddressesResponse

                        visProgress(progressState = false)

                        when {
                            response.success -> {
                                if (response.data == null || response.data.shipping.isNullOrEmpty()) {
                                    visLabel(true)
                                } else {
                                    visLabel(false)
                                    addressAdapter.submitData(response.data.shipping as ArrayList<AddressesResponse.Address>)
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

    override fun onRemoveAddressClickListener(
        addressModel: AddressesResponse.Address,
        viewHolder: AddressAdapter.ViewHolder,
        position: Int
    ) {

        viewModel.deleteAddress(
            user_id = shardHelper.id,
            address_id = addressModel.shipping_id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.deleteAddressStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        viewHolder.visProgress(true)
                    }
                    is NetworkState.Error -> {
                        viewHolder.visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {

                        val response = it.response as GeneralResponse

                        viewHolder.visProgress(false)

                        when {
                            response.success -> {

                                Utils.toast(mContext, getString(R.string.address_removed_succ))

                                addressAdapter.removeAddress(addressModel)

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    override fun onAddressClickListener(addressModel: AddressesResponse.Address) {

        val selectAddress =
            (mContext as AddressesActivity).intent.extras!!.getBoolean(Constants.SELECT_ADDRESS)

        if (selectAddress) {

            val args = Intent()
            args.putExtra(Constants.ADDRESS, addressModel as Serializable)

            (mContext as AddressesActivity).setResult(Activity.RESULT_OK, args)
            (mContext as AddressesActivity).finish()
        }

    }

    override fun onUpdateAddressClickListener(
        addressModel: AddressesResponse.Address
    ) {

        val simpleAddressModel = SimpleAddressModel(
            alter_contact = addressModel.alter_contact,
            alter_name = addressModel.alter_name,
            city = addressModel.city,
            city_arabic = addressModel.city_arabic,
            city_id = addressModel.city_id,
            city_name = addressModel.city_name,
            district_name = addressModel.district_name,
            is_selected = addressModel.is_selected,
            latitude = addressModel.latitude,
            location = addressModel.location,
            longitude = addressModel.longitude,
            mem_id = addressModel.mem_id,
            shipping_charge = addressModel.shipping_charge,
            shipping_id = addressModel.shipping_id
        )

        val bundle = Bundle()
        bundle.putSerializable(Constants.DATA, simpleAddressModel as Serializable)
        bundle.putBoolean(Constants.UPDATE_ADDRESS, true)

        navController.navigate(R.id.addAddressFragment, bundle)
    }

    private fun visProgress(progressState: Boolean) {

        if (progressState) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
        }

    }

    private fun visLabel(state: Boolean) {

        if (state) {
            binding.tvEmptyAddresses.visibility = View.VISIBLE
        } else {
            binding.tvEmptyAddresses.visibility = View.GONE
        }

    }

    private fun setUp() {

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow_2)
    }

    */

//        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)




        lifecycleScope.launchWhenStarted{
            addressAdapter.sizeState.collect {

                if (it == 0)
                    visLabel(true)
                else
                    visLabel(false)

            }
        }
    }

    companion object {
//        private val TAG = this::class.java.name
    }
}