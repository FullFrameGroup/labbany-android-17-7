package com.labbany.labbany.ui.basket.payment

/*
import net.geidea.paymentsdk.GeideaPaymentAPI
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.flow.pay.PaymentIntent
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.PaymentMethod

class TestPaymentSdk(var mContext: Context, private var paymentLauncher: ActivityResultLauncher<PaymentIntent>)  {

    private lateinit var paymentIntent: PaymentIntent

    init {
        onCreate()
    }

    private fun onCreate() {

        GeideaPaymentAPI.serverEnvironment = ServerEnvironment.Prod

        // Check if your credentials are not stored on device
        if (!GeideaPaymentAPI.hasCredentials) {
            // Store them so on next app start they could be re-used by the SDK
            GeideaPaymentAPI.setCredentials(
                merchantKey = "72b39716-f971-4319-a98e-37f179053406",
                merchantPassword = "985a2e41-65b7-4c30-aba4-e36d52a3e371"
            )
        }

      */
/*  // Register the SDK PaymentContract to receive a result with the order
        paymentLauncher =(mContext as FragmentActivity).registerForActivityResult(PaymentContract(), ::handleOrderResult)
*//*

       }

    fun startPayment(){

        paymentIntent = PaymentIntent {
            // Mandatory properties
            amount = "123.45".toBigDecimal()
            currency = "SAR"
            paymentMethod = PaymentMethod {
                cardHolderName = "M M"
                cardNumber = "5123450000000008"
                expiryDate = ExpiryDate(
                    month = 1,
                    year = 25
                )
                cvv = "123"
            }
            // Optional properties
            callbackUrl = "https://website.hook/calbackurl"
            merchantReferenceId = "1234"
            customerEmail = "email@test.com"
            billingAddress = Address(
                countryCode = "BGR",
                city = "Sofia",
                street = "Vitosha",
                postCode = "1000"
            )
            shippingAddress = Address(
                countryCode = "BGR",
                city = "Sofia",
                street = "Vitosha",
                postCode = "1000"
            )
        }
        launchPaymentIntent()

    }

    private fun launchPaymentIntent() {
        // Start the payment flow now!
        paymentLauncher.launch(paymentIntent)
    }



     fun showSuccessResult(text: String) {
        showTextResult("Success", text)
    }

     fun showErrorResult(text: String) {
        showTextResult("Error", text)
    }

  */
/*  private fun showErrorMessage(message: String?) {
        AlertDialog.Builder(mContext)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }*//*


    private fun showTextResult(title: String, text: String) {
        val contentView = LayoutInflater.from(mContext)
            .inflate(R.layout.dialog_help, null)

        val textView: TextView = contentView.findViewById(R.id.tv_msg)!!
        textView.text = text

        AlertDialog.Builder(mContext)
            .setTitle(title)
            .setView(contentView)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

}*/
