package woowacourse.shopping.data.network.coupon.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("fixed")
data class FixedDiscountCouponDto(
    override val id: Long? = null,
    override val code: String = "",
    override val description: String,
    override val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    override val discountType: String? = "fixed",
) : CouponDto
