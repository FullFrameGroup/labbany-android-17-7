package com.labbany.labbany.pojo.response

data class GetawayResultResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val response: Response
    )

    data class Response(
        val amount: String,
        val billing: Billing,
        val buildNumber: String,
        val card: Card,
        val currency: String,
        val customParameters: CustomParameters,
        val customer: Customer,
        val descriptor: String,
        val id: String,
        val merchantInvoiceId: String?,
        val merchantTransactionId: String?,
        val ndc: String,
        val paymentBrand: String,
        val paymentType: String,
        val result: Result,
        val resultDetails: ResultDetails,
        val risk: Risk,
        val threeDSecure: ThreeDSecure,
        val timestamp: String
    )

    data class Billing(
        val country: String,
        val postcode: String,
        val state: String,
        val street1: String
    )

    data class Card(
        val bin: String,
        val binCountry: String,
        val country: String,
        val expiryMonth: String,
        val expiryYear: String,
        val holder: String,
        val issuer: Issuer,
        val last4Digits: String,
        val maxPanLength: String,
        val regulatedFlag: String,
        val type: String
    )

    data class CustomParameters(
        val CTPE_DESCRIPTOR_TEMPLATE: String,
        val SHOPPER_MSDKIntegrationType: String,
        val SHOPPER_MSDKVersion: String,
        val SHOPPER_OS: String,
        val SHOPPER_device: String
    )

    data class Customer(
        val email: String,
        val givenName: String,
        val ip: String,
        val ipCountry: String,
        val surname: String
    )

    data class Result(
        val code: String,
        val description: String
    )

    data class ResultDetails(
        val AcquirerResponse: String,
        val AuthCode: String,
        val ConnectorTxID1: String,
        val ConnectorTxID2: String,
        val ConnectorTxID3: String,
        val EXTERNAL_SYSTEM_LINK: String,
        val ExtendedDescription: String,
        val OrderID: String,
        val ProcStatus: String,
        val TermID: String,
        val clearingInstituteName: String
    )

    data class Risk(
        val score: String
    )

    data class ThreeDSecure(
        val eci: String,
        val paRes: String,
        val xid: String
    )

    data class Issuer(
        val bank: String,
        val phone: String,
        val website: String
    )
}