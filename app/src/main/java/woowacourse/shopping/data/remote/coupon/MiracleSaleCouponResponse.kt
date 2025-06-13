package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.MiracleSaleCoupon
import java.time.LocalDate

@Serializable
@SerialName("percentage")
data class MiracleSaleCouponResponse(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    val discount: Int,
    val availableTime: AvailableTime,
) : CouponsResponse {
    fun toDomain(): MiracleSaleCoupon =
        MiracleSaleCoupon(
            id,
            code,
            description,
            discountType,
            LocalDate.parse(expirationDate),
            discount,
            availableTime.toDomain(),
        )
}
