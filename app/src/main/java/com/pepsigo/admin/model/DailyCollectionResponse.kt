package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class DailyCollectionResponse(
    @SerializedName("date") val date: String,
    @SerializedName("data") val data: List<ExecutiveCollection> = emptyList()
)

data class ExecutiveCollection(
    @SerializedName("executive_id") val executiveId: Int,
    @SerializedName("executive_name") val executiveName: String,
    @SerializedName("total_collected") val totalCollected: String
)

