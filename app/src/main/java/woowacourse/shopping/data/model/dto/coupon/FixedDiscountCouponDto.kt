package woowacourse.shopping.data.model.dto.coupon

import com.google.gson.annotations.SerializedName

data class FixedDiscountCouponDto(
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

    @SerializedName("discount")
    val discount: Int,

    @SerializedName("minimumAmount")
    val minimumAmount: Int,
) : CouponDto()
