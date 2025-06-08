package woowacourse.shopping.data.network.response.coupons

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import java.time.LocalDate

@Serializable
@SerialName("fixed")
data class FixedCouponResponse(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    val minimumAmount: Int,
    val discount: Int,
) : CouponsResponse {
    fun toDomain(): Coupon {
        return FixedCoupon(
            id,
            code,
            description,
            discountType,
            LocalDate.parse(expirationDate),
            minimumAmount,
            discount,
        )
    }
}
