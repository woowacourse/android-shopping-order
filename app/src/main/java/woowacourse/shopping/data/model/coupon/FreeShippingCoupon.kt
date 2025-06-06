package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalDateSerializer
import java.time.LocalDate
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon as DomainFreeShippingCoupon

@Serializable
@SerialName("freeShipping")
data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
) : CouponResponse

fun FreeShippingCoupon.toDomain(): DomainFreeShippingCoupon =
    DomainFreeShippingCoupon(
        id,
        code,
        description,
        expirationDate,
        minimumAmount,
    )
