package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.repository.CartRepository

class FakeCartRepository(
    private val products: Map<Long, Product> = emptyMap(),
) : CartRepository {
    private val items = mutableMapOf<Long, Int>()

    override suspend fun getCart(): Cart {
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
    }

    override suspend fun deleteItem(productId: Long): RemoveItemResult =
        if (items.remove(productId) != null) {
            RemoveItemResult.Success(getCart())
        } else {
            RemoveItemResult.NotFoundItem
        }

    override suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ): Cart {
        if (amount <= 0) {
            items.remove(productId)
        } else {
            items[productId] = amount
        }
        return getCart()
    }
}
