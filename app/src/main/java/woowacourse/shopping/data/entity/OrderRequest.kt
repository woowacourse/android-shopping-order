package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<CartIdEntity>,
    val originalPrice: Int,
    val points: Int
)