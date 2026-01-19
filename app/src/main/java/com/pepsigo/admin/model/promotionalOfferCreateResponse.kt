package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PromotionalOfferCreateResponse(
    val message:String,
    @SerializedName("offer_id")
    val offerId:Int
)
