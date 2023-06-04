package woowacourse.shopping.data.cart.model.dto.response

import woowacourse.shopping.data.product.model.dto.response.ProductResponse

data class CartProductResponse(
    val id: Long,
    val quantity: Int,
    val product: ProductResponse,
)
