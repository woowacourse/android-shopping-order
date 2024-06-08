package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.Product

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: Product,
    val checked: Boolean,
)
