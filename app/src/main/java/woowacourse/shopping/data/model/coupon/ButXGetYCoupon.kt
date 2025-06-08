package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("buyXgetY")
data class ButXGetYCoupon(
    override val code: String,
    override val id: Long,
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
    override val discountType: String = "buyXgetY"
) : Coupon()