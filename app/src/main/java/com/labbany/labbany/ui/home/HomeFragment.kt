package com.labbany.labbany.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentHomeBinding
import com.labbany.labbany.pojo.model.Banner
import com.labbany.labbany.pojo.model.CityModel
import com.labbany.labbany.pojo.model.ProductModel
import com.labbany.labbany.pojo.response.ProductsResponse
import com.labbany.labbany.ui.MainActivity
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.ui.my_city.CitiesDialog
import com.labbany.labbany.ui.views.MainToolbar
import com.labbany.labbany.util.*
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.koin.android.ext.android.inject

class HomeFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var homeHeaderAdapter: HomeHeaderAdapter
    private lateinit var offersAdapter: OffersAdapter
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = requireContext()

        homeHeaderAdapter = HomeHeaderAdapter(this)
        offersAdapter = OffersAdapter(this)

        binding.rvProducts.adapter = homeHeaderAdapter
//        binding.rvProducts.isVerticalScrollBarEnabled=true

        setSlider()
        cityCheck()
        setPopUp()

        val dialogsListener = object : DialogsListener {
            override fun <T> onDismiss(data: T) {

                data as CityModel

//              //  Log.e(TAG, "onDismiss: city_id ${data.city_id}")
                products(data.city_id)
            }

        }

        MainToolbar(
            binding.tb,
            mContext,
            dialogsListener,
            (requireActivity() as MainActivity).basketLauncher
        )

        products(if (!auth() && shardHelper.cityId == 0) null else shardHelper.cityId)

        binding.swipeRefreshLayout.setOnRefreshListener {
            products(if (!auth() && shardHelper.cityId == 0) null else shardHelper.cityId)
        }

        onBack()
    }

    private fun setPopUp() {

        if (arguments != null)
            if (requireArguments().containsKey(Constants.COUPON_MESSAGE) && requireArguments().containsKey(
                    Constants.COUPON_CODE
                )
            ) {
                val bundle = Bundle()

                bundle.putString(
                    Constants.COUPON_MESSAGE,
                    requireArguments().getString(Constants.COUPON_MESSAGE)
                )
                bundle.putString(
                    Constants.COUPON_CODE,
                    requireArguments().getString(Constants.COUPON_CODE)
                )

                navController.navigate(R.id.newUserDialog, bundle)
            }


    }

    private fun cityCheck() {

        if (!auth() && shardHelper.cityId == 0) {
            openCityDialog()
        }

    }

    private fun openCityDialog() {

        val dialogsListener = object : DialogsListener {
            override fun <T> onDismiss(data: T) {
                data as CityModel

                binding.tb.icCity.text = data.city_name_arabic
                products(data.city_id)

            }
        }
        CitiesDialog(dialogsListener).show(parentFragmentManager, "")

    }

    private fun auth(): Boolean = shardHelper.isLogged()

    private fun products(cityID: Int?) {

        viewModel.products(
            cityID
        )

        lifecycleScope.launchWhenStarted {
            viewModel.productsStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                      //  Log.e(TAG, "products: 1")
                        return@collect
                    }
                    is NetworkState.Loading -> {
                      //  Log.e(TAG, "products: 2")
                        visProgress(true)
                    }
                    is NetworkState.Error -> {
                        visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as ProductsResponse

                      //  Log.e(TAG, "products: 3")
                        visProgress(false)

                        when {
                            response.success -> {

                              //  Log.e(TAG, "result: --")
                                homeHeaderAdapter.clear()

                                if (!response.data.categories.isNullOrEmpty()) {

                                    response.data.categories.forEach { category ->

                                        if (!category.products.isNullOrEmpty()) {
                                            Log.e(
                                                TAG,
                                                "result: category ${category.id} , ${category.categoryName}"
                                            )
                                            homeHeaderAdapter.submitData(category)
                                        }
                                    }

                                } else {
                                    HelperDialog(getString(R.string.no_products_in_this_city), null)
                                        .show(parentFragmentManager, "")
                                }

//                              //  Log.e(TAG, "result: banners ${response.data.banners}")
                                if (!response.data.banners.isNullOrEmpty()) {

//                                  //  Log.e(TAG, "result: banners ${response.data.banners.size}")
                                    binding.slider.visibility = View.VISIBLE
                                    offersAdapter.submitData(response.data.banners)

                                } else {
                                    binding.slider.visibility = View.GONE
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

    private fun setSlider() {

        binding.slider.setSliderAdapter(offersAdapter)
        binding.slider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.slider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        binding.slider.scrollTimeInSec = 3
        binding.slider.startAutoCycle()

    }

    private fun onBack() {

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })

    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            if (binding.swipeRefreshLayout.isRefreshing)
                binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun <UI, D> onRootClickListener(dataBinding: UI, data: D) {

        data as ProductModel

        val bundle = Bundle()
        bundle.putInt(Constants.PRODUCT_ID, data.productId)
        bundle.putString(Constants.PRODUCT_NAME, data.productName)
        if (!data.productImage.isNullOrEmpty())
            bundle.putString(Constants.IMAGE, data.productImage)

//        if (auth()) {
//      //  Log.e(TAG, "onRootClickListener: data.productId ${data.productId}")
        navController.navigate(R.id.productDetailsFragment, bundle)
//        } else
//            navController.navigate(R.id.notAuthDialog)


//        val productDetailsFragment=ProductDetailsFragment()
//        productDetailsFragment.arguments=bundle
//
//        productDetailsFragment.show(parentFragmentManager,"")

    }

    override fun <UI, D> onOfferClickListener(dataBinding: UI, data: D) {

        data as Banner

        val bundle = Bundle()

        if (!data.bannerdetails.isNullOrEmpty())
            bundle.putString(Constants.IMAGE, data.bannerdetails)

        navController.navigate(R.id.offerDetailsDialog, bundle)
    }

    companion object {
        private val TAG = this::class.java.name
    }
}