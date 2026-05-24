package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DiscountType {
    @SerialName("fixed")
    FIXED,

    @SerialName("buyXgetY")
    BUY_X_GET_Y,

    @SerialName("freeShipping")
    FREE_SHIPPING,

    @SerialName("percentage")
    PERCENTAGE,
}
