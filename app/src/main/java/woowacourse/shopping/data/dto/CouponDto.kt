package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class CouponDto(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String,
    @SerializedName("expirationDate") val expirationDate: String,
    @SerializedName("discount") val discount: Int? = null,
    @SerializedName("minimumAmount") val minimumAmount: Int? = null,
    @SerializedName("buyQuantity") val buyQuantity: Int? = null,
    @SerializedName("getQuantity") val getQuantity: Int? = null,
    @SerializedName("availableTime") val availableTime: AvailableTimeDto? = null,
    @SerializedName("discountType") val discountType: String,
)
