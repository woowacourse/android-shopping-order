package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("freeShipping")
data class FreeShippingCoupon(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int,
    override val discountType: String = "freeShipping"
) : Coupon()
