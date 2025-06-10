package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalDateSerializer
import java.time.LocalDate
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon as DomainPercentageDiscountCoupon

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
) : CouponResponse

fun PercentageDiscountCoupon.toDomain(): DomainPercentageDiscountCoupon =
    DomainPercentageDiscountCoupon(
        id,
        code,
        description,
        expirationDate,
        discount,
        availableTime.toDomain(),
    )
