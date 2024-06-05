package woowacourse.shopping.presentation.cart.order

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.cart.toDomain
import woowacourse.shopping.presentation.cart.toUiModel

data class OrderUiState(
    private val orderedProducts: List<CartProductUi> = emptyList(),
    val recommendProducts: List<CartProductUi> = emptyList(),
) {
    val orderedCart: Cart get() = Cart(orderedProducts.map { it.toDomain() })
    constructor(orderedCart: Cart, recommendProducts: List<CartProductUi>) : this(
        orderedProducts = orderedCart.cartProducts.map { it.toUiModel() },
        recommendProducts = recommendProducts,
    )

    private val totalProducts
        get() = orderedProducts + recommendProducts

    val totalOrderIds
        get() = totalProducts.map { it.product.id }
    val totalCount
        get() = totalProducts.sumOf { it.count }

    val totalPrice
        get() =
            totalProducts
                .sumOf { it.totalPrice }
}
