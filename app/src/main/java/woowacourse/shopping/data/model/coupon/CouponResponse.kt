package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
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
