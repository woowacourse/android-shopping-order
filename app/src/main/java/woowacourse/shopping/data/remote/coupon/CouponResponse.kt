package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.Coupon

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
sealed interface CouponsResponse {
    val id: Int
    val code: String
    val description: String
    val expirationDate: String
    val discountType: String
}

fun CouponsResponse.toDomain(): Coupon =
    when (this) {
        is BogoCouponResponse -> this.toDomain()
        is FixedCouponResponse -> this.toDomain()
        is FreeshippingCouponResponse -> this.toDomain()
        is MiracleSaleCouponResponse -> this.toDomain()
    }
