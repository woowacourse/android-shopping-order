package woowacourse.shopping.data.network.coupon.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("buyXgetY")
data class BuyXGetYCouponDto(
    override val id: Long? = null,
    override val code: String = "",
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    override val discountType: String? = "buyXgetY"
) : CouponDto
