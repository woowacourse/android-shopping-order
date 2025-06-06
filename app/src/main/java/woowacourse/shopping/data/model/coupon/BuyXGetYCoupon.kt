package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.util.LocalDateSerializer
import java.time.LocalDate

@Serializable
@SerialName("buyXgetY")
data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : CouponResponse
