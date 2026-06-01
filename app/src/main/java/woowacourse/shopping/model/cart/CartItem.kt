package woowacourse.shopping.model.cart

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.product.Product

data class CartItem(
    val id: Long? = null,
    val product: Product,
    val quantity: Int,
) {
    init {
        require(quantity >= 1) { "수량은 1 이상의 정수여야 합니다." }
    }

    val totalPrice: Money
        get() = product.price * quantity
}
