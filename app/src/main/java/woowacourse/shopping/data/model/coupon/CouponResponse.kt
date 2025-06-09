package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.Coupon

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
sealed class CouponResponse {
    @SerialName("code")
    abstract val code: String

    @SerialName("description")
    abstract val description: String

    @SerialName("expirationDate")
    abstract val expirationDate: String

    @SerialName("id")
    abstract val id: Long
}

fun CouponResponse.toTypedDomain(): Coupon =
    when (this) {
        is BogoCouponResponse -> this.toDomain()
        is FreeShippingCouponResponse -> this.toDomain()
        is TimeLimitedCouponResponse -> this.toDomain()
        is DiscountCouponResponse -> this.toDomain()
    }
