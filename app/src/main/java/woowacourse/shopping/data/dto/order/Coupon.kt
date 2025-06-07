package woowacourse.shopping.data.dto.order

import com.google.gson.annotations.SerializedName

data class Coupon(
    @SerializedName("availableTime")
    val availableTime: AvailableTime,
    @SerializedName("buyQuantity")
    val buyQuantity: Int,
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("expirationDate")
    val expirationDate: String,
    @SerializedName("getQuantity")
    val getQuantity: Int,
    @SerializedName("id")
    val id: Long,
    @SerializedName("minimumAmount")
    val minimumAmount: Int,
)

fun Coupon.toDomain(): woowacourse.shopping.order.Coupon =
    woowacourse.shopping.order.Coupon(
        availableTime = availableTime.toDomain(),
        buyQuantity = buyQuantity,
        code = code,
        description = description,
        discount = discount,
        discountType = discountType,
        expirationDate = expirationDate,
        getQuantity = getQuantity,
        id = id,
        minimumAmount = minimumAmount,
    )
