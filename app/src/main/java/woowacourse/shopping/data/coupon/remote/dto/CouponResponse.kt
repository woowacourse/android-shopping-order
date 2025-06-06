package woowacourse.shopping.data.coupon.remote.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.common.toBuyXGetY
import woowacourse.shopping.data.common.toFixed
import woowacourse.shopping.data.common.toFreeShipping
import woowacourse.shopping.data.common.toPercentage
import woowacourse.shopping.domain.coupon.Coupon

@Serializable
data class CouponResponse(
    val availableTime: AvailableTime? = null,
    val buyQuantity: Int? = null,
    val code: String,
    val description: String,
    val discount: Int? = null,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int? = null,
    val id: Int,
    val minimumAmount: Int? = null,
) {
    fun toDomain(): Coupon {
        return when (discountType) {
            "buyXgetY" -> toBuyXGetY()
            "fixed" -> toFixed()
            "freeShipping" -> toFreeShipping()
            "percentage" -> toPercentage()
            else -> throw IllegalArgumentException("${TYPE_NOT_FOUND}: $discountType")
        }
    }

    companion object {
        private const val TYPE_NOT_FOUND = "type not found"
    }
}
