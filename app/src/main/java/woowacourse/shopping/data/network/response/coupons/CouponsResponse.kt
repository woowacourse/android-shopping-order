package woowacourse.shopping.data.network.response.coupons

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.coupon.Coupon

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

fun CouponsResponse.toDomain(): Coupon {
    return when (this) {
        is BogoCouponResponse -> toDomain()
        is FixedCouponResponse -> toDomain()
        is FreeshippingCouponResponse -> toDomain()
        is MiracleSaleCouponResponse -> toDomain()
    }
}
