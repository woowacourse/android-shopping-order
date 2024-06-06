package woowacourse.shopping.ui.model

import woowacourse.shopping.remote.model.ProductDto

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: ProductDto,
    val checked: Boolean,
)
