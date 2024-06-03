package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@SerialName("percentage")
data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
    override val discountType: String = "percentage",
) : Coupon() {
    @Serializable
    data class AvailableTime(
        val start: String,
        val end: String,
    )
}
