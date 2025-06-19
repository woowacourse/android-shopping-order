package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.FreeshippingCoupon
import java.time.LocalDate

@Serializable
@SerialName("freeShipping")
data class FreeshippingCouponResponse(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    val minimumAmount: Int,
) : CouponsResponse {
    fun toDomain(): FreeshippingCoupon =
        FreeshippingCoupon(
            id,
            code,
            description,
            discountType,
            LocalDate.parse(expirationDate),
            minimumAmount,
        )
}
