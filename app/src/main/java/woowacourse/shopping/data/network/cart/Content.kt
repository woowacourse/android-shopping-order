package woowacourse.shopping.data.network.cart

import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

data class Content(
    val id: Int,
    val product: Product,
    val quantity: Int,
) {
    fun toDomain(): CartContent {
        return CartContent(
            product = this.product,
            quantity = this.quantity,
        )
    }
}
