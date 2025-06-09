package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("fixed")
data class FixedCouponResponse(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val minimumAmount: Int,
    override val discountType: String = "fixed",
) : CouponResponse()