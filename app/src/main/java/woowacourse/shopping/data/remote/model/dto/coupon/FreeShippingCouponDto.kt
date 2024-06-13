package woowacourse.shopping.data.remote.model.dto.coupon

import com.google.gson.annotations.SerializedName

data class FreeShippingCouponDto(
    @SerializedName("id")
    override val id: Long,

    @SerializedName("code")
    override val code: String,

    @SerializedName("description")
    override val description: String,

    @SerializedName("expirationDate")
    override val expirationDate: String,

    @SerializedName("discountType")
    override val discountType: String,

    @SerializedName("minimumAmount")
    val minimumAmount: Int,
) : CouponDto()
