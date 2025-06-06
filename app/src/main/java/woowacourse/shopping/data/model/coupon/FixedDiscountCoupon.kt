package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalDateSerializer
import java.time.LocalDate

@Serializable
@SerialName("fixed")
data class FixedDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
) : CouponResponse
