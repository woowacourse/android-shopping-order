package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.ui.model.CartItem

class DefaultShoppingCartRepository(
    private val cartSource: ShoppingCartDataSource,
) : ShoppingCartRepository {
    override fun loadAllCartItems(): List<CartItem> {
        return cartSource.loadAllCartItems().map {
            CartItem(
                id = it.id,
                product = it.product.toDomain(),
                quantity = it.quantity,
                checked = false,
            )
        }
    }

    override fun shoppingCartProductQuantity(): Int = cartSource.loadAllCartItems().sumOf { it.quantity }

    override fun updateProductQuantity(
        cartItemId: Long,
        quantity: Int,
    ) {
        cartSource.updateProductsCount(cartItemId, quantity)
    }

    override fun addShoppingCartProduct(
        productId: Long,
        quantity: Int,
    ) {
        cartSource.addNewProduct(ProductIdsCount(productId, quantity))
    }

    override fun removeShoppingCartProduct(cartItemId: Long) {
        cartSource.removeCartItem(cartItemId)
    }
}
