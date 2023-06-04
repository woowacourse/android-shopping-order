package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class PayRequest(
    val cartItemIds: List<CartIdEntity>,
    val originalPrice: Int,
    val points: Int
)