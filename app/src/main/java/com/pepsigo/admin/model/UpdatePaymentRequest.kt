package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class UpdatePaymentRequest(
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("ref_number") val refNumber: String? = null,
    @SerializedName("denomination") val denomination: DenominationRequest? = null
)

data class DenominationRequest(
    @SerializedName("denom_2000") val denom2000: Int = 0,
    @SerializedName("denom_500") val denom500: Int = 0,
    @SerializedName("denom_200") val denom200: Int = 0,
    @SerializedName("denom_100") val denom100: Int = 0,
    @SerializedName("denom_50") val denom50: Int = 0,
    @SerializedName("denom_20") val denom20: Int = 0,
    @SerializedName("denom_10") val denom10: Int = 0,
    @SerializedName("denom_5") val denom5: Int = 0,
    @SerializedName("denom_2") val denom2: Int = 0,
    @SerializedName("denom_1") val denom1: Int = 0,
    @SerializedName("card") val card: Double = 0.00,
    @SerializedName("upi") val upi: Double = 0.00,
    @SerializedName("net_banking") val netBanking: Double = 0.00,
    @SerializedName("cheque") val cheque: Double = 0.00,
    @SerializedName("credit") val credit: Double = 0.00
)
