package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName

data class Quantity(
    @SerialName("quantity") val quantity: Int,
)
