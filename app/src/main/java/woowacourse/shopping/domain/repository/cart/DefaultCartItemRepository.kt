package woowacourse.shopping.domain.repository.cart

import woowacourse.shopping.data.source.cart.CartItemDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.ui.model.CartItem

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource,
) : CartItemRepository {
    override fun loadPagedCartItem(): List<CartItem> {
        return cartItemDataSource.loadAllCartItems().map {
            CartItem(
                id = it.id,
                quantity = it.quantity,
                product = it.product,
                checked = false,
            )
        }
    }

    override fun addCartItem(
        id: Long,
        quantity: Int,
    ) {
        val all = cartItemDataSource.loadAllCartItems()
        val cartItem = all.find { it.product.id == id }

        if (cartItem == null) {
            cartItemDataSource.addedNewProductsId(ProductIdsCount(id, quantity))
            return
        }

        cartItemDataSource.plusProductsIdCount(cartItem.id, quantity = cartItem.quantity + quantity)
    }

    override fun removeCartItem(id: Long) {
        cartItemDataSource.removedProductsId(id)
    }

    override fun increaseCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val all = cartItemDataSource.loadAllCartItems()
        val cartItem = all.find { it.product.id == id } ?: throw NoSuchElementException()
        cartItemDataSource.plusProductsIdCount(cartItem.id, quantity)
    }

    override fun decreaseCartProduct(
        id: Long,
        quantity: Int,
    ) {
        cartItemDataSource.minusProductsIdCount(id, quantity)
    }
}
