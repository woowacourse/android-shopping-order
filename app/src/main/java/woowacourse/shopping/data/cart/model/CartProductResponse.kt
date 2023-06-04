package woowacourse.shopping.data.cart.model

import woowacourse.shopping.data.product.model.ProductResponse

data class CartProductResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int,
)
