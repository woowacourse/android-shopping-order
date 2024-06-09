package woowacourse.shopping.ui.model

import woowacourse.shopping.data.product.remote.dto.ProductDto

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: ProductDto,
    val checked: Boolean,
)
