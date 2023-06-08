package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderPostEntity(
    val cartItemIds: List<Long>,
    val cardNumber: String,
    val cvc: Int,
    val point: Int,
)
