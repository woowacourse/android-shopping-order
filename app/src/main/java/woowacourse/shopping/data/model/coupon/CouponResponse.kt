package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CouponResponse {
    @SerialName("code")
    abstract val code: String

    @SerialName("description")
    abstract val description: String

    @SerialName("discountType")
    abstract val discountType: String

    @SerialName("expirationDate")
    abstract val expirationDate: String

    @SerialName("id")
    abstract val id: Long
}
