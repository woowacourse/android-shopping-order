package woowacourse.shopping.domain.model

import woowacourse.shopping.data.remote.Product

data class Cart(
    val product: Product,
    var quantity: Int,
) {
    val totalPrice: Int get() = product.price * quantity
}
