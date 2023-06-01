package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductListDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartProduct

object ProductMapper {
    fun ProductDto.toCartProduct() = CartProduct(
        product = product,
        cartItem = cartItem ?: CartItem.getDefault(),
    )

    fun ProductListDto.toCartProducts(): List<CartProduct> {
        return this.products.map { it.toCartProduct() }
    }
}
