package com.labbany.labbany.util

object Constants {

    const val API_GENERAL_AUTH: String = "WYLUnBhBpGltg?##$%%^Y##$".plus("GZWcXfIW")
    const val PHONE_CUSTOMER_SERVICES = "966561455500"
    const val TRANSACTIONS_UP = 2
    const val TRANSACTIONS_DOWN = 3
    const val SOLVED = 4
    const val UN_SOLVED = 5
    const val BEARER = "Bearer"
    const val PHONE = "phone"
    const val DATA = "data"
    const val ACTION = "action"
    const val FORGET_PASSWORD = "forget_password"
    const val COUPON_CODE = "coupon_code"
    const val COUPON_MESSAGE = "coupon_message"
    const val DEVICE_TYPE = "android"
    const val APP_VERSION = "3.4.16"
    const val USER_ID = "user_id"
    const val PRODUCT_ID = "product_id"
    const val PRODUCT_NAME = "product_name"
    const val ORDER_ID = "order_id"
    const val VISA_TYPE_ID = "visa_type_id"
    const val VISA_TYPE_NAME = "visa_type_name"
    const val ON_VISA_SELECTED = "OnVisaSelected"
    const val IMAGE = "image"
    const val UPDATE_ADDRESS = "update_address"
    const val ADDRESS = "address"
    const val SELECT_ADDRESS = "select_address"
    const val PHOTO_URL = "photo_url"
    const val EMAIL = "email"
    const val NAME = "name"
    const val REGISTRATION_PARAMS = 1
    const val FORGET_PASSWORD_PARAMS = 0

    object RequestCode {
        const val USER_IMAGE_REQUEST_CODE = 12
        const val MEDIA_REQUEST_CODE = 13
        const val LOCATION_REQUEST_CODE = 23
        const val INTERNAL_PRODUCT_IMAGE_REQUEST_CODE = 141
        const val EXTERNAL_PRODUCT_IMAGE_REQUEST_CODE = 142
    }

    object ShardKeys {

        const val SHARD_NAME = "Labbany"
        const val EMAIL = "email"
        const val USER_ID = "id"
        const val IMAGE_URL = "image_url"
        const val CITY_NAME = "city_name"
        const val CITY_NAME_EN = "city_name_en"
        const val CITY_ID = "city_id"
        const val MOBILE = "mobile"
        const val NAME = "name"
        const val FCM_TOKEN = "access_token"
    }

    object Codes {
        const val EXCEPTIONS_CODE = 3421
        const val X_API_KEY_CODE = 401
        const val AUTH_CODE = 407
        const val WAITING_CODE = 100
        const val UNKNOWN_CODE = 560
        const val SUCCESSES_CODE = 200
        const val MEDIA_SIZE_OR_MIME_TYPE_CODE = 5034
    }

    object Auth {
        const val EMAIL_AND_PHONE_CODE = 503
        const val PASSWORD_CODE = 202
        const val PHONE_CODE = 203
        const val EMAIL_CODE = 204
        const val OTP_CODE = 205
        const val MEDIA_SIZE_OR_MIME_TYPE_CODE = 5034
    }

    object Coupons {
        const val Not_Found_CODE = 220
        const val CITY_CODE = 221
        const val MAX_CODE = 222
        const val NO_WALLET_CODE = 223
        const val EXPIRED_CODE = 224
        const val INCLUDING_TYPE = "OrderIncluding"
        const val EXCLUDING_TYPE = "OrderExcluding"
    }

    object Orders {
        const val CANCEL_ORDER = 225
        const val ACCEPTED = "Accepted"
        const val CASH_COLLECTED = "Cash Collected"
        const val PICKED_UP = "Picked up"
        const val DECLINED = "Declined"
        const val DELIVERED = "Delivered"
        const val READY_FOR_SHIP = "Ready For Ship"

    }

    object Answers {

        const val GOOD = "good"
        const val BAD = "bad"

    }

    object AuthTypes {
        const val NORMAL = 10
        const val SOCIAL = 12
    }

    object PaymentTypes {
        const val CASH = "CASH"
        const val VISA = "VISA"
    }

    object Visas {
        const val VISA_TYPE_CASH_ID = -10
        const val MADA_ID = 4
        const val VISA_ID = 1
        const val VISA_CODE = 213
        const val VISA_16_NUM_CODE = 213//16 رقم
        const val VISA_FOUNDED_CODE = 214//موجودة
        const val VISA_TYPE_CODE = 215//نوع البطاقة

    }
    object Config {
        /* The default amount and currency */
        const val AMOUNT = "49.99"
        const val CURRENCY = "EUR"

        /* The payment brands for Ready-to-Use UI and Payment Button */
        val PAYMENT_BRANDS = linkedSetOf("VISA", "MASTER", "PAYPAL", "GOOGLEPAY")

        /* The default payment brand for payment button */
        const val PAYMENT_BUTTON_BRAND = "GOOGLEPAY"

        /* The card info for SDK & Your Own UI */
        const val CARD_BRAND = "VISA"
        const val CARD_HOLDER_NAME = "JOHN DOE"
        const val CARD_NUMBER = "4200000000000000"
        const val CARD_EXPIRY_MONTH = "07"
        const val CARD_EXPIRY_YEAR = "21"
        const val CARD_CVV = "123"
    }

    enum class TransactionState {
        NEW,
        PENDING,
        COMPLETED
    }

}