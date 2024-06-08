package woowacourse.shopping.presentation.order.recommend

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.cart.toDomain
import woowacourse.shopping.presentation.cart.toUiModel

data class RecommendProductUiState(
    private val orderedProducts: List<CartProductUi> = emptyList(),
    val recommendProducts: List<CartProductUi> = emptyList(),
) {
    val orderedCart: Cart get() = Cart(orderedProducts.map { it.toDomain() })

    constructor(orderedCart: Cart, recommendProducts: List<CartProductUi>) : this(
        orderedProducts = orderedCart.cartProducts.map { it.toUiModel() },
        recommendProducts = recommendProducts,
    )

    val totalProducts
        get() = orderedProducts + recommendProducts.filter { it.count > 0 }

    val totalCount
        get() = totalProducts.sumOf { it.count }

    val totalPrice
        get() =
            totalProducts
                .sumOf { it.totalPrice }
}
