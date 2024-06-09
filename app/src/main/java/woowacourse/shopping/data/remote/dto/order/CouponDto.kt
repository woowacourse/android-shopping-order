package woowacourse.shopping.data.remote.dto.order

import com.google.gson.annotations.SerializedName

data class CouponDto(
    @SerializedName("id") val id: Long,
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String,
    @SerializedName("expirationDate") val expirationDate: String,
    @SerializedName("discount") val discount: Int?,
    @SerializedName("minimumAmount") val minimumAmount: Int?,
    @SerializedName("buyQuantity") val buyQuantity: Int?,
    @SerializedName("getQuantity") val getQuantity: Int?,
    @SerializedName("availableTime") val availableTime: AvailableTimeDto?,
    @SerializedName("discountType") val discountType: String,
) {
    data class AvailableTimeDto(
        @SerializedName("start") val start: String,
        @SerializedName("end") val end: String,
    )
}
