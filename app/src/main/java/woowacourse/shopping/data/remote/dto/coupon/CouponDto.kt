package woowacourse.shopping.data.remote.dto.coupon

import com.google.gson.annotations.SerializedName

data class CouponDto(
    @SerializedName("availableTime")
    val availableTimeDto: AvailableTimeDto? = null,
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discount")
    val discount: Int = 0,
    @SerializedName("minimumAmount")
    val minimumAmount: Int = 0,
    @SerializedName("buyQuantity")
    val buyQuantity: Int = 0,
    @SerializedName("getQuantity")
    val getQuantity: Int = 0,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("expirationDate")
    val expirationDate: String,
    @SerializedName("id")
    val id: Int,
)
