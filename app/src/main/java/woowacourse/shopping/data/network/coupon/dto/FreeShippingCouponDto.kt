package woowacourse.shopping.data.network.coupon.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FREE_SHIPPING")
data class FreeShippingCouponDto(
    override val id: Long? = null,
    override val code: String = "",
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int? = null,
    override val discountType: String? = "FREE_SHIPPING"

) : CouponDto
