package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@SerialName("freeShipping")
data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    override val discountType: String = "freeShipping",
) : Coupon()
