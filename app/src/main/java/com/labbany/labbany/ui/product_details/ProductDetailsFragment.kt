package com.labbany.labbany.ui.product_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentProductDetailsBinding
import com.labbany.labbany.pojo.model.CartModel
import com.labbany.labbany.pojo.model.PriceModel
import com.labbany.labbany.pojo.response.AddCartResponse
import com.labbany.labbany.pojo.response.ProductDetailsResponse
import com.labbany.labbany.ui.MainActivity
import com.labbany.labbany.ui.basket.BasketActivity
import com.labbany.labbany.util.*
import com.labbany.labbany.util.Utils.englishNumbers
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject

class ProductDetailsFragment : DialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var navController: NavController
    private val viewModel: ProductDetailsViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var packingAdapter: PricesAdapter
    private lateinit var sizeAdapter: PricesAdapter
    private lateinit var cuttingAdapter: PricesAdapter
    private lateinit var mincedMeatAdapter: PricesAdapter
    private lateinit var coinAdapter: SmallAdapter
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        navController = findNavController()
        mContext = requireContext()

        adapters()

        binding.tvAddToBasket.setOnClickListener {

            if (!verify()) return@setOnClickListener

            addToCart()
        }

        productDetails(shardHelper.cityId, requireArguments().getInt(Constants.PRODUCT_ID))

    }

    private fun addToCart() {

        if (!auth()) {
            navController.navigate(R.id.notAuthDialog)
            return
        }

        val cartModel = CartModel(
            cutId = if (binding.spChopping.count != 0) cuttingAdapter.getItem(binding.spChopping.selectedItemPosition).id else null,
            extraId = if (binding.spMincedMeat.count != 0) mincedMeatAdapter.getItem(binding.spMincedMeat.selectedItemPosition).id else null,
            notes = if (!binding.etNotes.text.isNullOrEmpty()) binding.etNotes.text.toString() else null,
            packingId = if (binding.spEncapsulation.count != 0) packingAdapter.getItem(binding.spEncapsulation.selectedItemPosition).id else null,
            pid = requireArguments().getInt(Constants.PRODUCT_ID),
            qty = if (!binding.etCount.text.isNullOrEmpty()) binding.etCount.text.toString()
                .englishNumbers()
                .toInt() else null,
            sizeId = if (binding.spSize.count != 0) sizeAdapter.getItem(binding.spSize.selectedItemPosition).id else null
        )

        viewModel.addToCart(shardHelper.id, cartModel)

        lifecycleScope.launchWhenStarted {
            viewModel.addToCartStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(true, resultState = false)
                    }
                    is NetworkState.Error -> {
                        visProgress(false, resultState = false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as AddCartResponse

                        visProgress(false, resultState = true)

                        when {
                            response.success -> {

                                (requireActivity() as MainActivity).basketLauncher.launch(
                                    Intent(
                                        mContext,
                                        BasketActivity::class.java
                                    )
                                )

                                mContext.startActivity(Intent(mContext, BasketActivity::class.java))
                                dismiss()

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun adapters() {

        packingAdapter = PricesAdapter()
        sizeAdapter = PricesAdapter()
        cuttingAdapter = PricesAdapter()
        mincedMeatAdapter = PricesAdapter()
        coinAdapter = SmallAdapter()

        binding.spEncapsulation.adapter = packingAdapter
        binding.spMincedMeat.adapter = mincedMeatAdapter
        binding.spSize.adapter = sizeAdapter
        binding.spChopping.adapter = cuttingAdapter

        binding.spCoin.adapter = coinAdapter

        binding.spChopping.onItemSelectedListener = this
        binding.spSize.onItemSelectedListener = this
        binding.spMincedMeat.onItemSelectedListener = this
        binding.spEncapsulation.onItemSelectedListener = this

        coinAdapter.submitData(viewModel.coins(requireContext()))

        binding.etCount.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                Log.e(TAG, "afterTextChanged: 1")
//                if (!s.isNullOrEmpty()) {
//                    val numbers = Utils.englishNumbers(s.toString())

//                    if (numbers.toLong() == 0L)
//                        binding.etCount.setText("1")
                /* else
                     binding.etCount.setText(numbers)*/
                calcTotalPrice()
                calcProductPrice()
//                }
            }
        })

    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        calcTotalPrice()
        calcProductPrice()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun productDetails(cityID: Int, productId: Int) {

        if (requireArguments().containsKey(Constants.IMAGE))
            if (!requireArguments().getString(Constants.IMAGE).isNullOrEmpty())
                Picasso.get().load(requireArguments().getString(Constants.IMAGE))
                    .into(binding.imgProduct)

        if (!requireArguments().getString(Constants.PRODUCT_NAME).isNullOrEmpty())
            binding.tv1.text = requireArguments().getString(Constants.PRODUCT_NAME)

        viewModel.productDetails(
            if (!auth() && shardHelper.cityId == 0) 1 else cityID,
            productId
        )

        lifecycleScope.launchWhenStarted {
            viewModel.productDetailsStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(progressState = true, resultState = false)
                    }
                    is NetworkState.Error -> {
                        visProgress(false, resultState = false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as ProductDetailsResponse

                        val data = response.data.product_detail[0]

                        when {
                            response.success -> {

                                visProgress(progressState = false, resultState = true)

                                if (!data.size.isNullOrEmpty()) {
                                    data.size.forEach { p ->
                                        sizeAdapter.submitData(
                                            PriceModel(
                                                p.size_id,
                                                p.size_type,
                                                if (p.size_price.isNullOrEmpty()) 0f else p.size_price.toFloat()
                                            )
                                        )
                                    }
                                }

                                if (!data.cut_piece.isNullOrEmpty()) {
                                    data.cut_piece.forEach { p ->
                                        cuttingAdapter.submitData(
                                            PriceModel(
                                                p.cut_piece_id,
                                                p.cut_piece_type,
                                                if (p.cut_piece_price.isNullOrEmpty()) 0f else p.cut_piece_price.toFloat()
                                            )
                                        )
                                    }
                                }
                                if (!data.packing.isNullOrEmpty()) {
                                    data.packing.forEach { p ->
                                        packingAdapter.submitData(
                                            PriceModel(
                                                p.packing_id,
                                                p.packing_type,
                                                if (p.packing_price.isNullOrEmpty()) 0f else p.packing_price.toFloat()
                                            )
                                        )
                                    }
                                }
                                if (!data.extra.isNullOrEmpty()) {
                                    data.extra.forEach { p ->
                                        mincedMeatAdapter.submitData(
                                            PriceModel(
                                                p.extra_id,
                                                p.extra_type,
                                                if (p.extra_price.isNullOrEmpty()) 0f else p.extra_price.toFloat()
                                            )
                                        )
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

    private fun calcProductPrice(): Float {

        var total = 0f
        var sum = 0f

        if (binding.spSize.count != 0)
            sum += sizeAdapter.getItem(binding.spSize.selectedItemPosition).price

        if (!binding.etCount.text.isNullOrEmpty())
            total += sum * binding.etCount.text.toString().englishNumbers().toInt()

        setProductPriceToTV(total)

        return total
    }

    @SuppressLint("SetTextI18n")
    private fun setProductPriceToTV(price: Float) {

        binding.tvPrice.text = Utils.decimalFormat(price)

    }

    private fun calcTotalPrice(): Float {

        var total = 0f
        var sum = 0f

        if (binding.spMincedMeat.count != 0)
            sum += mincedMeatAdapter.getItem(binding.spMincedMeat.selectedItemPosition).price

        if (binding.spChopping.count != 0)
            sum += cuttingAdapter.getItem(binding.spChopping.selectedItemPosition).price

        if (binding.spEncapsulation.count != 0)
            sum += packingAdapter.getItem(binding.spEncapsulation.selectedItemPosition).price

        if (binding.spSize.count != 0)
            sum += sizeAdapter.getItem(binding.spSize.selectedItemPosition).price

        if (!binding.etCount.text.isNullOrEmpty())
            total += sum * binding.etCount.text.toString().englishNumbers().toInt()

        setTotalPriceToTV(total)

        return total
    }

    @SuppressLint("SetTextI18n")
    private fun setTotalPriceToTV(price: Float) {

        binding.tvFinalTotal.text = "${Utils.decimalFormat(price)} ${getString(R.string.sar)}"

    }

    private fun visProgress(progressState: Boolean, resultState: Boolean) {

        if (progressState) {
            binding.tvAddToBasket.visibility = View.GONE
            binding.incProgress.root.visibility = View.VISIBLE
        } else {
            binding.tvAddToBasket.visibility = if (resultState) View.VISIBLE else View.INVISIBLE
            binding.incProgress.root.visibility = View.GONE
        }

    }

    private fun verify(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etCount, null)) isNull = true
        else if (binding.etCount.text.toString() == "0") {

            binding.etCount.error = getString(R.string.count_should_be_g_0)
            isNull = true
        }

        return !isNull
    }

    private fun setUp() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            })

    }

    private fun auth(): Boolean = shardHelper.isLogged()

    companion object {
        private val TAG = this::class.java.name
    }

}