package woowacourse.shopping.data.network.response.carts

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.cart.ShoppingCart

@Serializable
data class CartsResponse(
    val content: List<Content>,
    val last: Boolean,
) {
    @Serializable
    data class Content(
        val id: Long,
        val product: Product,
        val quantity: Int,
    ) {
        fun toDomain(): ShoppingCart {
            return ShoppingCart(
                id,
                product.toDomain(),
                Quantity(quantity),
            )
        }
    }

    fun toDomain(): CartsSinglePage {
        val shoppingCart = content.map { it.toDomain() }
        return CartsSinglePage(
            shoppingCart,
            last,
        )
    }
}
