package woowacourse.shopping.data.model

import woowacourse.shopping.presentation.model.CartModel

data class OrderDetailEntity(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderedAt: String,
    val products: List<CartModel>,
)
