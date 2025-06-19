package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.BogoCoupon
import java.time.LocalDate

@Serializable
@SerialName("buyXgetY")
data class BogoCouponResponse(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : CouponsResponse {
    fun toDomain(): BogoCoupon =
        BogoCoupon(
            id,
            code,
            description,
            discountType,
            LocalDate.parse(expirationDate),
            buyQuantity,
            getQuantity,
        )
}
