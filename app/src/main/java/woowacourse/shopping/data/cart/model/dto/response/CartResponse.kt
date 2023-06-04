package woowacourse.shopping.data.cart.model.dto.response

import woowacourse.shopping.data.product.model.dto.PaginationDto

data class CartResponse(
    val cartItems: List<CartProductResponse>,
    val pagination: PaginationDto,
)
