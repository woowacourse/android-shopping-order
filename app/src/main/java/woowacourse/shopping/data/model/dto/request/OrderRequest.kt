package woowacourse.shopping.data.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Long>,
    val cardNumber: String,
    val cvc: Int,
    val point: Int,
)
