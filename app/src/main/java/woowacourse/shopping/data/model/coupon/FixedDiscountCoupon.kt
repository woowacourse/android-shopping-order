package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalDateSerializer
import java.time.LocalDate
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon as DomainFixedDiscountCoupon

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

fun FixedDiscountCoupon.toDomain(): DomainFixedDiscountCoupon =
    DomainFixedDiscountCoupon(
        id,
        code,
        description,
        expirationDate,
        discount,
        minimumAmount,
    )
