package com.labbany.labbany.util.hyperpay

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity

class CheckoutBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action

        if (CheckoutActivity.ACTION_ON_BEFORE_SUBMIT == action) {
            val paymentBrand = intent.getStringExtra(CheckoutActivity.EXTRA_PAYMENT_BRAND)
            val checkoutId = intent.getStringExtra(CheckoutActivity.EXTRA_CHECKOUT_ID)
            val senderComponentName: ComponentName? = intent.getParcelableExtra(
                CheckoutActivity.EXTRA_SENDER_COMPONENT_NAME
            )

            /* You can use this callback to request a new checkout ID if selected payment brand requires
               some specific parameters or just send back the same checkout id to continue checkout process */
            val newIntent = Intent(CheckoutActivity.ACTION_ON_BEFORE_SUBMIT)
            newIntent.component = senderComponentName
            newIntent.setPackage(senderComponentName!!.packageName)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            newIntent.putExtra(CheckoutActivity.EXTRA_CHECKOUT_ID, checkoutId)

            /* You also can abort the transaction by sending the CheckoutActivity.EXTRA_TRANSACTION_ABORTED */
            newIntent.putExtra(CheckoutActivity.EXTRA_TRANSACTION_ABORTED, true)

            context.startActivity(newIntent)
        }
    }
}