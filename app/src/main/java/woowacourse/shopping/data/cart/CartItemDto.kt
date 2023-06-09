package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.ProductDto
import woowacourse.shopping.domain.CartItem
import java.time.LocalDateTime

data class CartItemDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto
) {
    fun toDomain(): CartItem {
        return CartItem(id, product.toDomain(), LocalDateTime.now(), quantity)
    }
}
