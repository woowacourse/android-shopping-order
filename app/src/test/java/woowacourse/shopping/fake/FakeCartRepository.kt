package woowacourse.shopping.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RemoveItemResult

class FakeCartRepository(
    private val products: Map<Long, Product> = emptyMap(),
) : CartRepository {
    private val items = mutableMapOf<Long, Int>()
    private val _cart = MutableStateFlow(Cart())

    override val cart = _cart.asStateFlow()

    override suspend fun loadCart() = Unit

    private fun currentCart(): Cart {
        val cartItems =
            items.mapNotNull { (id, quantity) ->
                products[id]?.let { CartItem(it, quantity) }
            }
        return Cart(cartItems)
    }

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ) {
        items[id] = (items[id] ?: 0) + quantity
        _cart.value = currentCart()
    }

    override suspend fun deleteItem(productId: Long): RemoveItemResult =
        if (items.remove(productId) != null) {
            _cart.value = currentCart()
            RemoveItemResult.Success
        } else {
            RemoveItemResult.NotFoundItem
        }

    override suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ) {
        if (amount <= 0) {
            items.remove(productId)
        } else {
            items[productId] = amount
        }
        _cart.value = currentCart()
    }
}
