package com.labbany.labbany.util.hyperpay

import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode
import com.oppwa.mobile.connect.provider.Connect

object HyperUtils {

    fun createCheckoutSettings(checkoutId: String): CheckoutSettings {
        return CheckoutSettings(checkoutId, Constants.Config.PAYMENT_BRANDS,
            Connect.ProviderMode.LIVE)
            .setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
    }

    enum class PaymentMode(var value:String){
        Development("development"),Production("production")
    }
}