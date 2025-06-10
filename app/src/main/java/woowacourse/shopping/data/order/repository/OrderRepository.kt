package woowacourse.shopping.data.order.repository

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

interface OrderRepository {
    suspend fun removeProductsFromShoppingCart(shoppingCartItem: List<ShoppingCartProduct>): Result<Unit>
}
