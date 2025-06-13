package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedCoupon
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
    fun toDomain(): Coupon =
        FixedCoupon(
            id,
            code,
            description,
            discountType,
            LocalDate.parse(expirationDate),
            minimumAmount,
            discount,
        )
}
