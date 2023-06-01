package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.ShoppingCartDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartProduct

object ShoppingCartMapper {
    fun ShoppingCartDto.toCartProduct() = CartProduct(
        product = product,
        cartItem = CartItem(id, quantity),
    )
}
