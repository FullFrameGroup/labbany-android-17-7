package com.labbany.labbany.ui.basket

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentBasketBinding
import com.labbany.labbany.pojo.model.CartOrderModel
import com.labbany.labbany.pojo.model.VisaModel
import com.labbany.labbany.pojo.model.VisaTypeModel
import com.labbany.labbany.pojo.request.MakeOrderRequest
import com.labbany.labbany.pojo.response.*
import com.labbany.labbany.ui.addresses.AddressesActivity
import com.labbany.labbany.ui.basket.delete_cart_item.DeleteCartItemDialog
import com.labbany.labbany.ui.basket.payment.OnCardBrandSelected
import com.labbany.labbany.ui.basket.times.TimesDialog
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.ui.loading.LoaderDialog
import com.labbany.labbany.ui.views.SharedViewModel
import com.labbany.labbany.util.*
import com.labbany.labbany.util.hyperpay.CheckoutBroadcastReceiver
import com.labbany.labbany.util.hyperpay.HyperUtils.createCheckoutSettings
import com.oppwa.mobile.connect.checkout.meta.CheckoutActivityResult
import com.oppwa.mobile.connect.checkout.meta.CheckoutActivityResultContract
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings
import com.oppwa.mobile.connect.exception.PaymentError
import com.oppwa.mobile.connect.exception.PaymentException
import com.oppwa.mobile.connect.payment.CheckoutInfo
import com.oppwa.mobile.connect.payment.PaymentParams
import com.oppwa.mobile.connect.provider.Connect
import com.oppwa.mobile.connect.provider.ITransactionListener
import com.oppwa.mobile.connect.provider.OppPaymentProvider
import com.oppwa.mobile.connect.provider.ThreeDSWorkflowListener
import com.oppwa.mobile.connect.provider.Transaction
import com.oppwa.mobile.connect.provider.threeds.v2.model.ThreeDSConfig
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.util.*

class BasketActivity : AppCompatActivity(), RecyclerViewOnClickListener/*, OnVisaSelected*/,
    Serializable,
    OnCardBrandSelected, ITransactionListener {

    private var checkoutId: String = ""
    private lateinit var binding: FragmentBasketBinding
    private val viewModel: BasketViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private val sharedViewModel: SharedViewModel by viewModel()
    private lateinit var basketAdapter: BasketAdapter
    private lateinit var visaTypeAdapter: VisaTypeAdapter
    private lateinit var loadingDialog: LoaderDialog
    private var mCalendar: Calendar? = null
    private var mTime: TimeSlotResponse.TimeSlot? = null
    private var selectedVisa: VisaModel? = null
    private var coupon: OrderCouponResponse.Coupon? = null
    private var addressModel: AddressesResponse.Address? = null
    private var fullTotalBeforeDiscount = 0.0
    private var fullTotalAfterDisCount = 0.0
    private var mCartData: CartResponse.Data? = null
    private var originPrice = 0.0
    private var orderId = 0
    private lateinit var mContext: Context
    private lateinit var mFragmentManager: FragmentManager
    private var visDate = false
    private val dateClickStateFlow = MutableStateFlow(true)
    private val timeClickStateFlow = MutableStateFlow(true)
    private lateinit var paymentProvider: OppPaymentProvider
    private var transactionState = TransactionState.NEW
    private lateinit var  threeDSConfig: ThreeDSConfig

    val threeDSWorkflowListener: ThreeDSWorkflowListener = object : ThreeDSWorkflowListener {
        override fun onThreeDSChallengeRequired(): Activity {
            // provide your Activity
            return this@BasketActivity
        }

        override fun onThreeDSConfigRequired(): ThreeDSConfig {
            return threeDSConfig
        }
    }

    private val addressesResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data =
                    result.data!!.extras!!.getSerializable(Constants.ADDRESS) as AddressesResponse.Address

                addressModel = data
                sharedViewModel.selectAddressForOrderState.value = false
                binding.tvAddress.text = addressModel!!.location
            }
        }

    private val checkoutLauncher =
        registerForActivityResult(CheckoutActivityResultContract()) { result: CheckoutActivityResult ->
            handleCheckoutResult(result)
        }

    private fun handleCheckoutResult(result: CheckoutActivityResult) {

        if (result.isCanceled) {
            // shopper cancelled the checkout process
            visProgress(false, false)
            return
        }

        val resourcePath = result.resourcePath

        if (resourcePath != null) {
            // request payment status using the resourcePath
            paymentStatus(resourcePath!!)
        }
    }

    /*private fun handleOrderResult(result: GeideaResult<Order>) {
        when (result) {
            is GeideaResult.Success<Order> -> {
                // Payment was successful
                // showSuccessResult(result.data.toJson(pretty = true))

              //  Log.e(TAG, "handleOrderResult: ${result.data}")
//                Toast.makeText(mContext, "Payment Success", Toast.LENGTH_SHORT).show()
                sendPaymentDetails(result.data)

            }
            is GeideaResult.Error -> {
                // Some error occurred
              //  Log.e(TAG, "handleOrderResult: $result")
                //result.toJson(pretty = true)
                showErrorResult("خطأ في بطاقة الدفع المدخلة \nرجاء تأكد منها أولا")
            }
            is GeideaResult.Cancelled -> {
                // The payment flow was intentionally cancelled by the user
              //  Log.e(TAG, "handleOrderResult: Cancelled")
                showErrorResult(" تم إلغاء عملية الدفع")
            }
        }
    }*/
    /*
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Inflate the layout for this fragment
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_basket, container, false)
            return binding.root
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*}

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
    */
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_basket)
        mContext = this
        mFragmentManager = supportFragmentManager

        init()
        initPayment()

        loadingDialog = LoaderDialog(mContext)
        basketAdapter = BasketAdapter(this)
        visaTypeAdapter = VisaTypeAdapter(this)

        binding.rvBasket.adapter = basketAdapter
        binding.rvVisa.adapter = visaTypeAdapter

        binding.tvAddBuy.setOnClickListener {
            visaTypeAdapter.setCashSelected(mContext)

            if (visaTypeAdapter.selectedVisaType == null) {
                showHelperDialog(getString(R.string.empty_payment_type))
                return@setOnClickListener
            }

            validateMakeOrder()
        }

        listenVisaSelected()

//      //  Log.e(TAG, "onViewCreated: ${shardHelper.id}")

        listenAmount()  

        binding.tvDate.setOnClickListener { showDateListener() }
        binding.tvTime.setOnClickListener { showTimeListener() }

        binding.tv3.setOnClickListener { goToAddress() }
        binding.img1.setOnClickListener { goToAddress() }
        binding.tvAddress.setOnClickListener { goToAddress() }

        data()
        observe()

        binding.tvAddCoupon.setOnClickListener {

            if (!verifyCoupon()) return@setOnClickListener

            callCouponApi()

        }

        binding.tb.imgBack.setOnClickListener { finish() }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {

        lifecycleScope.launchWhenStarted {
            viewModel.cartDetailStateFlow.collect {
                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }

                    is NetworkState.Loading -> {
                        visProgress(progressState = true, resultState = false)
                    }

                    is NetworkState.Error -> {
                        visProgress(progressState = false, resultState = false)
                        it.handleErrors(mContext, null)
                    }

                    is NetworkState.Result<*> -> {
                        val response = it.response as CartResponse

                        visProgress(progressState = false, resultState = false)

                        when {
                            response.success -> {
                                ui(response)
                            }

                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.makeOrderStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }

                    is NetworkState.Loading -> {
                        visProgress(true, resultState = true)
                    }

                    is NetworkState.Error -> {
                        visProgress(false, resultState = true)
                        it.handleErrors(mContext, null)
                    }

                    is NetworkState.Result<*> -> {
                        val response = it.response as MakeOrderResponse

                        visProgress(false, resultState = true)

                        when {
                            response.success -> {

                                orderId = response.data.order_id

                                if (visaTypeAdapter.getPaymentType() == "CASH")
                                    goToMyOrder(response.data.order_id)
                                else {
                                    Utils.toast(
                                        mContext,
                                        "تم الطلب بنجاح جاري الانتقال الي عملية الدفع"
                                    )

                                    requestCheckoutId(getCartId())
                                }
                            }

                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.checkoutIdStateFlow.collect {
                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }

                    is NetworkState.Loading -> {
                        visProgress(progressState = true, resultState = false)
                    }

                    is NetworkState.Error -> {
                        visProgress(progressState = false, resultState = false)
                        it.handleErrors(mContext, null)
                    }

                    is NetworkState.Result<*> -> {
                        val response = it.response as CheckoutIdResponse

                        when {
                            response.success -> {
                                checkoutId = response.checkoutId
                                startPayment(response.checkoutId)
                            }

                            else -> {
                                visProgress(progressState = false, resultState = false)
                                NetworkState.Error(response.code)
                                    .handleErrors(mContext)
                            }
                        }

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getawayResultStateFlow.collect {
                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }

                    is NetworkState.Loading -> {
                        visProgress(progressState = true, resultState = false)
                    }

                    is NetworkState.Error -> {
                        visProgress(progressState = false, resultState = false)
                        it.handleErrors(mContext, null)
                    }

                    is NetworkState.Result<*> -> {
                        val data = it.response as JsonObject

//                      //  Log.e(TAG, "hyper pay transaction: 8.1 $data")

                        val response = Gson().fromJson(data, GetawayResultResponse::class.java)

//                      //  Log.e(TAG, "hyper pay transaction: 8 $response")

                        when {
                            data.get("success").asBoolean -> {
                                sendPaymentDetails(response.data.response)
                            }

                            else -> {
                                visProgress(progressState = false, resultState = false)
                                NetworkState.Error(response.code, data.get("msg").asString)
                                    .handleErrors(mContext, object : DialogsListener {
                                        override fun onDismiss() {
                                            finish()
                                        }
                                    })
                            }
                        }

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sendPaymentDetailStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }

                    is NetworkState.Loading -> {
                        visProgress(progressState = true, resultState = false)
                    }

                    is NetworkState.Error -> {
                        visProgress(progressState = false, resultState = false)
                        it.handleErrors(mContext, null)
                    }

                    is NetworkState.Result<*> -> {
                        val response = it.response as GeneralResponse

                        visProgress(progressState = false, resultState = false)

                        if (response.success)
                            goToMyOrder(orderId)
                        else
                            NetworkState.Error(response.code)

                    }
                }
            }
        }

    }

    private fun getCartId(): Int {

        return ((viewModel.cartDetailStateFlow.value as NetworkState.Result<*>).response as CartResponse).data!!.cart_id
    }

    @SuppressLint("SetTextI18n")
    private fun ui(response: CartResponse) {
        if (response.data == null) {

            emptyBasketDialog()

        } else {
            visProgress(progressState = false, resultState = true)

            mCartData = response.data

            visaTypeAdapter.addCashType(mContext)

            if (!response.data.visa_types.isNullOrEmpty())
                visaTypeAdapter.submitData(response.data.visa_types as ArrayList<VisaTypeModel>)

            if (!response.data.cart_items.isNullOrEmpty()) {
                basketAdapter.submitData(response.data.cart_items as ArrayList<CartOrderModel>)

//              //  Log.e(TAG, "result: cart items ${response.data.cart_items}")
                response.data.cart_items.forEach { i ->
                    originPrice += i.total_price
                }

                setItemPrice()

//              //  Log.e(TAG, "result: total $fullTotalBeforeDiscount")
            } else {
                emptyBasketDialog()
            }
            binding.tvWallet.text =
                "${Utils.decimalFormat(response.data.wallet_amount)} ${
                    getString(
                        R.string.sar
                    )
                }"

            if (response.data.date_setting.open == "on")
                visDateViews(true)
            else
                visDateViews(false)

        }
    }

    private fun init() {
        sharedViewModel.visaStateFlow.value = NetworkState.Idle
        sharedViewModel.selectAddressData.value = NetworkState.Idle
    }

    private fun listenAmount() {

        binding.sw.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.etWalletAmount.visibility = View.VISIBLE
            else
                binding.etWalletAmount.visibility = View.GONE

        }

    }

    private fun callCouponApi() {

        viewModel.checkOrderCoupon(
            shardHelper.id,
            binding.etAddCoupon.text.toString(),
            shardHelper.cityId
        )

        lifecycleScope.launchWhenStarted {
            viewModel.checkOrderCouponStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }

                    is NetworkState.Loading -> {
                        visCouponProgress(true)
                    }

                    is NetworkState.Error -> {
                        visCouponProgress(false)
                        it.handleErrors(mContext, null)
                    }

                    is NetworkState.Result<*> -> {
                        val response = it.response as OrderCouponResponse

                        visCouponProgress(false)

                        when {
                            response.success -> {
                                showCouponMessage(
                                    getString(R.string.success_coupon),
                                    true,
                                    response.data!!.Coupon
                                )
                            }

                            response.code == Constants.Coupons.Not_Found_CODE -> {
                                showCouponMessage(getString(R.string.not_found_coupon), false)
                            }

                            response.code == Constants.Coupons.EXPIRED_CODE -> {
                                showCouponMessage(getString(R.string.expired_coupon), false)
                            }

                            response.code == Constants.Coupons.CITY_CODE -> {
                                showCouponMessage(getString(R.string.no_city_coupon), false)
                            }

                            response.code == Constants.Coupons.MAX_CODE -> {
                                showCouponMessage(getString(R.string.max_coupon), false)
                            }

                            response.code == Constants.Coupons.NO_WALLET_CODE -> {
                                showCouponMessage(getString(R.string.not_wallet_coupon), false)
                            }

                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun showHelperDialog(msg: String, mDialogsListener: DialogsListener? = null) =
        HelperDialog(msg, mDialogsListener).show(mFragmentManager, "")

    private fun visCouponProgress(s: Boolean) {

        if (s) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvAddCoupon.visibility = View.INVISIBLE
            binding.tvCoupon.visibility = View.GONE
            binding.etAddCoupon.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.tvAddCoupon.visibility = View.VISIBLE
            binding.etAddCoupon.isEnabled = true
            binding.tvCoupon.visibility = View.VISIBLE
        }

    }

    private fun showCouponMessage(
        msg: String,
        success: Boolean,
        co: OrderCouponResponse.Coupon? = null
    ) {

        if (success) {
            binding.tvCoupon.setTextColor(ContextCompat.getColor(mContext, R.color.green))
            co?.coupon_code = binding.etAddCoupon.text.toString()
            coupon = co
            setItemPrice()
//            basketAdapter.applyCoupon(coupon!!)
        } else
            binding.tvCoupon.setTextColor(ContextCompat.getColor(mContext, R.color.red))

        binding.tvCoupon.text = msg
    }

    private fun goToAddress() {

        val intent = Intent(mContext, AddressesActivity::class.java)
        intent.putExtra(Constants.SELECT_ADDRESS, true)

        addressesResultLauncher.launch(intent)

    }

    private fun listenVisaSelected() {

        lifecycleScope.launchWhenStarted {
            sharedViewModel.visaStateFlow.collect {

//              //  Log.e(TAG, "listenVisaSelected: visaStateFlow $it")

                when (it) {
                    is NetworkState.Error -> {
                    }

                    is NetworkState.Idle -> {
                    }

                    is NetworkState.Loading -> {
                    }

                    is NetworkState.Result<*> -> {
                        selectedVisa = it.response as VisaModel

//                      //  Log.e(TAG, "listenVisaSelected: visa $it")

                        checkOrderSentBefore()
                    }
                }


            }
        }

    }

    private fun validateMakeOrder() {

        if (!verifyBasket()) return

        callMakeOrderApi()

    }

    private fun checkOrderSentBefore() {

        when (viewModel.makeOrderStateFlow.value) {

            is NetworkState.Error -> {
            }

            is NetworkState.Idle -> {
                validateMakeOrder()
            }

            is NetworkState.Loading -> {
            }

            is NetworkState.Result<*> -> {

                val response =
                    (viewModel.makeOrderStateFlow.value as NetworkState.Result<*>).response as MakeOrderResponse

                when {
                    response.success -> {

                        requestCheckoutId(getCartId())
                    }

                    else -> NetworkState.Error(response.code)
                        .handleErrors(mContext)

                }
            }
        }


    }

    private fun showDateListener() {

        if (!dateClickStateFlow.value) return

        dateClickStateFlow.value = false

        val mDateListener = object : DateListener {
            override fun onFullTimeListener(calendar: Calendar) {

                mCalendar = calendar
                mTime = null

                binding.tvDate.text = Utils.dateFormat(calendar)
                binding.tvTime.text = mContext.getString(R.string.select_order_time)

                dateClickStateFlow.value = true
            }

            override fun onCancel() {
                dateClickStateFlow.value = true
            }

        }

//        Utils.datePicker(mContext, mDateListener)
        if (mCartData != null)
            Utils.someDateAtDatePicker(
                mContext,
                mFragmentManager,
                mCartData!!.date_setting.avaliable_days,
                mDateListener
            )
    }

    private fun showTimeListener() {

        if (!timeClickStateFlow.value) return

        if (mCartData != null && mCalendar != null) {

            timeClickStateFlow.value = false
            val date = Utils.simpleDateFormat(mCalendar!!).plus(" ").plus(Utils.fullTimeFormat())

//          //  Log.e(TAG, "showTimeListener: $date")

            val mDialogsListener = object : DialogsListener {
                @SuppressLint("SetTextI18n")
                override fun onResult(time: TimeSlotResponse.TimeSlot) {

                    mTime = time

                    binding.tvTime.text =
                        "${mContext.getString(R.string.from)} ${time.from_time} ${
                            mContext.getString(
                                R.string.to
                            )
                        } ${time.to_time}"

                    timeClickStateFlow.value = true
                }

                override fun onCancel() {
                    timeClickStateFlow.value = true
                }
            }

            TimesDialog(date, mDialogsListener).show(mFragmentManager, "")

        } else if (mCalendar == null) {
            showHelperDialog(getString(R.string.delivery_date_first))
        }

    }

    @SuppressLint("SetTextI18n")
    private fun data() {

        viewModel.cartDetail(shardHelper.id)

    }

    override fun onDeleteCartItemClickListener(cartOrderModel: CartOrderModel, position: Int) {

        val dialogsListener = object : DialogsListener {
            override fun onResult() {

                Utils.toast(mContext, getString(R.string.cart_item_removed_succ))

                basketAdapter.deleteItem(cartOrderModel, position)

            }

        }

        DeleteCartItemDialog(
            mCartData!!.cart_id,
            cartOrderModel.cart_item_id,
            dialogsListener
        ).show(
            mFragmentManager,
            ""
        )

    }

    private fun callMakeOrderApi() {

        /*try {
          //  Log.e(TAG, "callMakeOrderApi: 1 ${Utils.getDateOnly(mCalendar!!)}")

        } catch (e: Exception) {
        }

        try {
          //  Log.e(TAG, "callMakeOrderApi: 2 ${mTime!!.from_time} | ${mTime!!.to_time}")

        } catch (e: Exception) {
        }*/

        val makeOrderRequest = MakeOrderRequest(
            additional_discount = null,
            campaign_code = null,
            campaign_id = null,
            campaign_type = null,
            campaign_value = null,
            cart_id = mCartData!!.cart_id,
            cart_items = basketAdapter.getBasket(),
            coupon_code = if (coupon != null) coupon!!.coupon_code!! else null,
            coupon_id = if (coupon != null) coupon!!.coupon_id else null,
            date = if (mCalendar == null) null else Utils.getDateOnly(mCalendar!!),
            discount = if (coupon != null) fullTotalBeforeDiscount - fullTotalAfterDisCount else null,
            payment_type = visaTypeAdapter.getPaymentType(),
            sd_amount = if (binding.sw.isChecked) binding.etWalletAmount.text.toString() else "0.0",
            ship_charge = mCartData!!.shipping!!,
            ship_id = addressModel!!.shipping_id,
            tax = mCartData!!.tax!!,
            time = if (mTime == null) null else "${mTime!!.from_time} | ${mTime!!.to_time}",
            total_payment = if (coupon != null) fullTotalAfterDisCount else fullTotalBeforeDiscount,
            user_id = shardHelper.id

        )

        viewModel.makeOrder(
            makeOrderRequest
        )

    }

    private fun goToMyOrder(orderId: Int) {

        val dialogsListener = object : DialogsListener {
            override fun onDismiss() {

                val intent = Intent()
                intent.putExtra(Constants.ORDER_ID, orderId)
                setResult(RESULT_OK, intent)

                finish()
            }
        }

        showHelperDialog(
            getString(R.string.make_order_succ),
            dialogsListener
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setItemPrice() {

        if (!mCartData!!.cart_items.isNullOrEmpty()) {

            val taxPercentage = mCartData!!.tax!! / 100

            if (coupon != null && !coupon!!.is_amount && coupon!!.minimum_amount <= originPrice) {

                val discountPercentage = 1 - coupon!!.percentage / 100

                fullTotalBeforeDiscount =
                    originPrice * (1 + taxPercentage) + mCartData!!.shipping!!

                fullTotalAfterDisCount =
                    if (coupon!!.CouponType == Constants.Coupons.INCLUDING_TYPE)
                        (originPrice * (1 + taxPercentage) + mCartData!!.shipping!!) * discountPercentage
                    else
                        (originPrice * discountPercentage) + originPrice * taxPercentage + mCartData!!.shipping!!

                binding.tvTotalDiscount.visibility = View.VISIBLE

                binding.tvTotalPrice.text =
                    "${Utils.decimalFormat(fullTotalAfterDisCount)} ${getString(R.string.sar_2)}"
                binding.tvTotalDiscount.text =
                    "${Utils.decimalFormat(fullTotalBeforeDiscount)} ${getString(R.string.sar_2)}"

            } else if (coupon != null && coupon!!.is_amount && coupon!!.minimum_amount <= originPrice) {

                fullTotalBeforeDiscount =
                    originPrice * (1 + taxPercentage) + mCartData!!.shipping!!

                fullTotalAfterDisCount =
                    if (coupon!!.CouponType == Constants.Coupons.INCLUDING_TYPE)
                        (originPrice * (1 + taxPercentage) + mCartData!!.shipping!!) - coupon!!.amount
                    else
                        (originPrice - coupon!!.amount) + originPrice * taxPercentage + mCartData!!.shipping!!

                binding.tvTotalDiscount.visibility = View.VISIBLE

                binding.tvTotalPrice.text =
                    "${Utils.decimalFormat(fullTotalAfterDisCount)} ${getString(R.string.sar_2)}"
                binding.tvTotalDiscount.text =
                    "${Utils.decimalFormat(fullTotalBeforeDiscount)} ${getString(R.string.sar_2)}"

            } else if (coupon != null) {

                fullTotalBeforeDiscount =
                    originPrice * (1 + taxPercentage) + mCartData!!.shipping!!

                fullTotalAfterDisCount = fullTotalBeforeDiscount

                binding.tvTotalPrice.text =
                    "${Utils.decimalFormat(fullTotalAfterDisCount)} ${getString(R.string.sar_2)}"
            } else {

                fullTotalBeforeDiscount =
                    originPrice * (1 + taxPercentage) + mCartData!!.shipping!!

//            fullTotalBeforeDiscount = fullTotalAfterDisCount

                binding.tvTotalPrice.text =
                    "${Utils.decimalFormat(fullTotalBeforeDiscount)} ${getString(R.string.sar_2)}"
            }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTotalPriceChanged(totalOld: Double, totalNew: Double) {

        val d = totalNew - totalOld

        originPrice += d

        setItemPrice()
    }

    override fun onCardBrandSelected(cardBrand: VisaTypeModel): Boolean {

      //  Log.e(TAG, "onCardBrandSelected: 1")

        /*        val isValidate = paymentValidate()

                if (!isValidate) return false

        //        testPaymentSdk= TestPaymentSdk(mContext,paymentLauncher)

                val bundle = Bundle()
                bundle.putInt(Constants.VISA_TYPE_ID, cardBrand.visa_type_id)
                bundle.putString(Constants.VISA_TYPE_NAME, cardBrand.visa_type_name)
        //        bundle.putSerializable(Constants.ON_VISA_SELECTED, this as OnVisaSelected)

                if (!cardBrand.visa_type_img.isNullOrEmpty())
                    bundle.putString(Constants.IMAGE, cardBrand.visa_type_img)

                navController.navigate(R.id.visaDetailsFragment, bundle)*/

        checkOrderSentBefore()

        return true
    }

    /* override fun onVisaSelected(visa: VisaModel) {

       //  Log.e(TAG, "onVisaSelected: 2 @@ $visa")

         startPayment()

     }*/

    private fun emptyBasketDialog() {

        HelperDialog(getString(R.string.no_carts), object : DialogsListener {
            override fun onDismiss() {
                visProgress(progressState = false, resultState = false)
                finish()
            }
        })
            .show(mFragmentManager, "")

    }

    private fun paymentValidate(): Boolean {

        if (basketAdapter.isEmptyBasket()) {
            showHelperDialog(getString(R.string.empty_basket))
            return false
        }

        if (addressModel == null) {
            showHelperDialog(getString(R.string.delivery_address_first))
            return false
        }

        if (binding.sw.isChecked) {
            if (!Utils.validateET(binding.etWalletAmount, null)) {
                showHelperDialog(getString(R.string.wallet_first))
                return false
            } else if (mCartData != null
                && mCartData!!.wallet_amount != 0.0
                && binding.etWalletAmount.text.toString()
                    .toDouble() > mCartData!!.wallet_amount
            ) {

                showHelperDialog(getString(R.string.enter_wallet_first))
                return false
            }
        }

        if (visDate) {
            if (mCalendar == null) {
                binding.svRoot.scrollTo(binding.tvDate.scrollX, binding.tvDate.scrollY)
                showHelperDialog(getString(R.string.delivery_date_first))
                return false
            }
            /*  if (mTime == null) {
                  binding.svRoot.scrollTo(binding.tvTime.scrollX, binding.tvTime.scrollY)
                  showHelperDialog(getString(R.string.delivery_time_first))
                  return false
              }*/
        }

        return true

    }

    private fun verifyCoupon(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etAddCoupon, getString(R.string.order_coupon))) isNull = true

        return !isNull
    }

    private fun verifyBasket(): Boolean {

//        val isNull = false

        if (basketAdapter.isEmptyBasket()) {
            showHelperDialog(getString(R.string.empty_basket))
            return false
        }

        if (addressModel == null) {
            showHelperDialog(getString(R.string.delivery_address_first))
            return false
        }

        if (binding.sw.isChecked) {
            if (!Utils.validateET(binding.etWalletAmount, null)) {
                showHelperDialog(getString(R.string.wallet_first))
                return false
            } else if (mCartData != null
                && mCartData!!.wallet_amount != 0.0
                && binding.etWalletAmount.text.toString()
                    .toDouble() > mCartData!!.wallet_amount
            ) {

                showHelperDialog(getString(R.string.enter_wallet_first))
                return false
            }
        }

        if (visDate) {
            if (mCalendar == null) {
                binding.svRoot.scrollTo(binding.tvDate.scrollX, binding.tvDate.scrollY)
                showHelperDialog(getString(R.string.delivery_date_first))
                return false
            }
            /*   if (mTime == null) {
                   binding.svRoot.scrollTo(binding.tvTime.scrollX, binding.tvTime.scrollY)
                   showHelperDialog(getString(R.string.delivery_time_first))
                   return false
               }*/
        }

        return true
    }

    private fun visDateViews(s: Boolean) {

        visDate = s

        if (s) {
            binding.tv6.visibility = View.VISIBLE
            binding.tvDate.visibility = View.VISIBLE
            binding.tvTime.visibility = View.VISIBLE
            binding.img3.visibility = View.VISIBLE
            binding.imgDate.visibility = View.VISIBLE
            binding.view4.visibility = View.VISIBLE
        } else {
            binding.tv6.visibility = View.GONE
            binding.tvDate.visibility = View.GONE
            binding.tvTime.visibility = View.GONE
            binding.img3.visibility = View.GONE
            binding.imgDate.visibility = View.GONE
            binding.view4.visibility = View.GONE
        }

    }

    private fun visProgress(progressState: Boolean, resultState: Boolean) {

        try {

            if (progressState) {
                binding.tvAddBuy.visibility = View.GONE

                if (!loadingDialog.isShowing) {
                    run {
                        try {
                            loadingDialog.show()
                        } catch (e: Exception) {
                        }
                    }
                }

            } else {
                if (resultState) {
                    binding.tvAddBuy.visibility = View.VISIBLE
                    binding.svRoot.visibility = View.VISIBLE
                    binding.tvEmptyBasket.visibility = View.GONE
                } else {
                    binding.tvAddBuy.visibility = View.INVISIBLE
                    binding.svRoot.visibility = View.INVISIBLE
                    binding.tvEmptyBasket.visibility = View.VISIBLE
                }

//            if (!loadingDialog.isHidden)
                loadingDialog.hide()
            }

        } catch (e: Exception) {
        }
    }

    private fun initPayment() {

        threeDSConfig = ThreeDSConfig.Builder()
            .setThreeDSRequestorAppUrlUsed(true)
            .build()

        paymentProvider = OppPaymentProvider(mContext, Connect.ProviderMode.LIVE)
        paymentProvider.setThreeDSWorkflowListener(threeDSWorkflowListener)
    }

    private fun requestCheckoutId(cartId: Int) {

        viewModel.requestCheckoutId(
            user_id = shardHelper.id,
            user_name = shardHelper.userName,
            email = shardHelper.email,
            city = shardHelper.cityNameEn,
            amount = if (coupon != null) fullTotalAfterDisCount
            else fullTotalBeforeDiscount,
            cartId = cartId,
            payment_method = visaTypeAdapter.getPaymentType(), addressModel!!
        )

    }

    private fun startPayment(checkoutId: String) {

        /* val year = "2025"
 //            (selectedVisa!!.cart_date.split("-")[0].toDouble() % 2000).roundToInt().toString()
         val month = "12"//selectedVisa!!.cart_date.split("-")[1]

       //  Log.e(TAG, "startPayment: fullTotalAfterDisCount $fullTotalAfterDisCount")

         val paymentParams: PaymentParams = CardPaymentParams(
             checkoutId,
             "VISA",// selectedVisa!!.visa_type,
             "4111111111111111",
             "ABC",
             month,
             year,
             "123"
         )*/
        // Set shopper result URL
//        paymentParams.shopperResultUrl = "labbany://result"

//        launchPaymentIntent(paymentParams)
        openCheckoutUI(checkoutId)
    }


    private fun openCheckoutUI(checkoutId: String) {


        val checkoutSettings =
            createCheckoutSettings(checkoutId)
        val configBuilder = ThreeDSConfig.Builder()
        checkoutSettings.setThreeDS2Config(configBuilder.build())

        checkoutSettings.threeDS2Config = threeDSConfig

            /* Set componentName if you want to receive callbacks from the checkout */
        val componentName =
            ComponentName(mContext.packageName, CheckoutBroadcastReceiver::class.java.name)

        /* Set up the Intent and start the checkout activity */
//        val intent = checkoutSettings.createCheckoutActivityIntent(mContext, componentName)

        checkoutLauncher.launch(checkoutSettings)
//        hyperResultLauncher.launch(intent)
    }


    private fun launchPaymentIntent(paymentParams: PaymentParams) {
        // Start the payment flow now!
        try {
            val transaction = Transaction(paymentParams)
            paymentProvider.submitTransaction(transaction, this)
        } catch (ee: PaymentException) {
            /* error occurred */
        }
    }

    private fun showErrorResult(text: String) {
        HelperDialog(text, null).show(mFragmentManager, null)
    }

    override fun transactionCompleted(p0: Transaction) {
      //  Log.e(TAG, "hyper pay transaction: 1 ${p0.brandSpecificInfo}")

        getResourcePath(p0.paymentParams.checkoutId)
    }

    override fun transactionFailed(p0: Transaction, p1: PaymentError) {
//      //  Log.e(TAG, "hyper pay transaction: tf ${p1.errorInfo}")
//      //  Log.e(TAG, "hyper pay transaction: tf ${p1.errorCode.ordinal}")
//      //  Log.e(TAG, "hyper pay transaction: tf ${p1.errorCode.name}")
//      //  Log.e(TAG, "hyper pay transaction: tf ${p1.errorCode.errorCode}")
//      //  Log.e(TAG, "hyper pay transaction: tf ${p1.errorCode.describeContents()}")
//      //  Log.e(TAG, "hyper pay transaction: tf ${p1.errorMessage}")
        showErrorResult(p1.errorMessage)
    }

    override fun paymentConfigRequestSucceeded(checkoutInfo: CheckoutInfo) {
        /* get the resource path in this ITransactionListener callback */
      //  Log.e(TAG, "hyper pay transaction: 4")
        val resourcePath = checkoutInfo.resourcePath
        paymentStatus(resourcePath!!)
      //  Log.e(TAG, "hyper pay transaction: 5")

    }

    private fun getResourcePath(checkoutId: String) {

        try {
          //  Log.e(TAG, "hyper pay transaction: 2")
            paymentProvider.requestCheckoutInfo(checkoutId, this)
        } catch (e: PaymentException) {
            /* error occurred */
          //  Log.e(TAG, "hyper pay transaction: 3")
        }

    }

    private fun paymentStatus(resourcePath: String) {

      //  Log.e(TAG, "hyper pay transaction: 6")
        viewModel.paymentStatus(resourcePath, visaTypeAdapter.getPaymentType())
    }

    private fun sendPaymentDetails(data: GetawayResultResponse.Response) {

        val year =
            data.card.expiryYear//(selectedVisa!!.cart_date.split("-")[0].toDouble() % 2000).roundToInt()
        val month = data.card.expiryMonth//selectedVisa!!.cart_date.split("-")[1].toInt()

        viewModel.sendPaymentDetails(
            user_id = shardHelper.id,
            total = if (coupon != null) fullTotalAfterDisCount else fullTotalBeforeDiscount,
            transaction_id = if (data.merchantTransactionId.isNullOrEmpty()) null else data.merchantTransactionId,
            checkoutId = data.merchantInvoiceId ?: "",
            payment_details = Gson().toJson(
                data,
                GetawayResultResponse.Response::class.java
            ),// data.toString()
            card_number = data.card.bin + "***" + data.card.last4Digits,//selectedVisa!!.card_number,
            card_holder_name = data.card.holder,//selectedVisa!!.card_holder_name,
            card_expair_date = "$month/$year",
            order_id = orderId
        )

    }

    override fun onResume() {
        super.onResume()

      //  Log.e(TAG, "onResume: 12")

        if (transactionState == TransactionState.PENDING) {
          //  Log.e(TAG, "onResume: 13")
        } else if (transactionState == TransactionState.COMPLETED) {
            paymentStatus("/v1/checkouts/${checkoutId}/payment")
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

      //  Log.e(TAG, "hyper pay transaction: onNewIntent")

        if (intent.scheme == "com.labbany") {
            /* request payment status */
            transactionState = TransactionState.COMPLETED
        }
    }


    enum class TransactionState {
        NEW,
        PENDING,
        COMPLETED
    }

    companion object {
        internal val TAG = this::class.java.name
    }

}