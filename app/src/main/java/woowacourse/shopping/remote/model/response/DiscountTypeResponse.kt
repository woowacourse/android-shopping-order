package woowacourse.shopping.remote.model.response

import kotlinx.serialization.SerialName

enum class DiscountTypeResponse {
    @SerialName("fixed")
    FIXED,

    @SerialName("buyXgetY")
    BUY_X_GET_Y,

    @SerialName("freeShipping")
    FREE_SHIPPING,

    @SerialName("percentage")
    PERCENTAGE,
}
