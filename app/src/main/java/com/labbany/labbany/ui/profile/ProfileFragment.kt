package com.labbany.labbany.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentProfileBinding
import com.labbany.labbany.pojo.model.CityModel
import com.labbany.labbany.pojo.model.UserModel
import com.labbany.labbany.pojo.model.UserModelBySerializable
import com.labbany.labbany.pojo.response.ProfileResponse
import com.labbany.labbany.ui.MainActivity
import com.labbany.labbany.ui.addresses.AddressesActivity
import com.labbany.labbany.ui.auth.logout.LogoutDialog
import com.labbany.labbany.ui.my_city.CitiesDialog
import com.labbany.labbany.ui.views.MainToolbar
import com.labbany.labbany.util.*
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import java.io.Serializable

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController
    private val viewModel: ProfileViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireView())
        mContext = requireContext()

        if (!auth()) return

        callApi()


        MainToolbar(
            binding.tb,
            mContext,
            null,
            (requireActivity() as MainActivity).basketLauncher
        )

        binding.tvMyOrders.setOnClickListener {
            navController.navigate(R.id.myOrdersFragment)
        }

        binding.tvCouponExchange.setOnClickListener {
            navController.navigate(R.id.couponExchangeFragment)
        }

        binding.tvChangePassword.setOnClickListener {
            navController.navigate(R.id.changePasswordFragment)
        }

        binding.tvCity.setOnClickListener {

            openCityDialog()

        }

        binding.tvAddress.setOnClickListener {
            navController.navigate(R.id.addressFragment)
        }

        binding.tvAddress.setOnClickListener {
            startActivity(
                Intent(
                    mContext,
                    AddressesActivity::class.java
                ).putExtra(Constants.SELECT_ADDRESS, false)
            )
        }

        binding.tvPaymentMethods.setOnClickListener {
            navController.navigate(R.id.paymentMethodsFragment)
        }

        binding.layoutProfileHeader.tvEdit.setOnClickListener {
            navController.navigate(R.id.editProfileFragment)
        }

        binding.tvConstraints.setOnClickListener {
            navController.navigate(R.id.termsAndConditionsFragment)
        }

        binding.tvRateUs.setOnClickListener {
            Utils.rateApp(requireContext())
        }

        binding.tvCallInfo.setOnClickListener {
            Utils.call(requireContext(), Constants.PHONE_CUSTOMER_SERVICES)
        }

        binding.tvUploadProblem.setOnClickListener {
            navController.navigate(R.id.complaintsFragment)
        }

        binding.tvLogout.setOnClickListener {
            openLogoutDialog()
        }

    }

    private fun auth(): Boolean = if (shardHelper.isLogged())
        true
    else {
        navController.navigate(R.id.notAuthDialog)
        false
    }

    private fun openLogoutDialog() {

        LogoutDialog(object : DialogsListener {
            override fun onDismiss() {
                navController.navigate(R.id.loginFragment)
            }
        }).show(parentFragmentManager, "")
    }

    private fun callApi() {

        viewModel.profile(
            shardHelper.id
        )

        lifecycleScope.launchWhenStarted {
            viewModel.profileStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(state = true, success = false)
                    }
                    is NetworkState.Error -> {
                        visProgress(state = false, success = false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as ProfileResponse

                        when {
                            response.success -> {

                                visProgress(state = false, success = true)
//                              //  Log.e(TAG, "result: done $response")

                                handleUIAndEditClick(response.data!!)

                            }
                            else -> {
                                visProgress(state = false, success = false)
                                NetworkState.Error(response.code)
                                    .handleErrors(mContext)
                            }
                        }

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleUIAndEditClick(data: UserModel) {

        binding.layoutProfileHeader.tvName.text = data.username
        binding.layoutProfileHeader.tvEmail.text = data.email
        binding.layoutProfileHeader.tvWallet.text = "${data.wallet} ${getString(R.string.sar)}"

        if (!data.image.isNullOrEmpty())
            Picasso.get().load(data.image).into(binding.layoutProfileHeader.civUser)

//      //  Log.e(TAG, "handleUIAndEditClick: data $data")

        binding.layoutProfileHeader.tvEdit.setOnClickListener {
            val userData = UserModelBySerializable(
                data.city_id,
                data.city_name,
                data.email,
                data.fcm_token,
                data.id,
                data.image,
                data.username,
                data.phone,
                data.wallet,
                data.token
            )

            val bundle = Bundle()
            bundle.putSerializable(Constants.DATA, userData as Serializable)

            navController.navigate(R.id.editProfileFragment, bundle)
        }

    }

    private fun visProgress(state: Boolean, success: Boolean) {

        if (state) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.layoutProfileHeader.root.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            if (success)
                binding.layoutProfileHeader.root.visibility = View.VISIBLE
            else
                binding.layoutProfileHeader.root.visibility = View.INVISIBLE
        }

    }


    private fun openCityDialog() {

        val dialogsListener = object : DialogsListener {
            override fun <T> onDismiss(data: T) {
                data as CityModel

                binding.tb.icCity.text = data.city_name_arabic
            }
        }
        CitiesDialog(dialogsListener).show(parentFragmentManager, "")

    }

    companion object {
        private val TAG = this::class.java.name
    }

}