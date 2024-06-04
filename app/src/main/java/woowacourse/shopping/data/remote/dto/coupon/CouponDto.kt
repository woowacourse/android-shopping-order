package woowacourse.shopping.data.remote.dto.coupon


import com.google.gson.annotations.SerializedName

data class CouponDto(
    @SerializedName("availableTime")
    val availableTime: AvailableTime? = null,
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discount")
    val discount: Int = 0,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("expirationDate")
    val expirationDate: String,
    @SerializedName("id")
    val id: Int
)
