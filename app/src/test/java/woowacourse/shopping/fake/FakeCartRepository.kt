package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.model.UpdateItemResult
import woowacourse.shopping.domain.repository.CartRepository

class FakeCartRepository(
    private val products: Map<Long, Product> = emptyMap(),
) : CartRepository {
    private val items = mutableMapOf<Long, Int>()

    override suspend fun getCart(): Cart {
        val cartItems =
            items.mapNotNull { (id, quantity) ->
                products[id]?.let {
                    CartItem(
                        id = 1L,
                        product = it,
                        quantity = quantity,
                    )
                }
            }
        return Cart(cartItems)
    }

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ): AddItemResult {
        val wasInCart = items.containsKey(id)
        items[id] = (items[id] ?: 0) + quantity
        val updatedCart = getCart()
        return if (wasInCart) {
            AddItemResult.Incremented(updatedCart)
        } else {
            AddItemResult.NewAdded(updatedCart)
        }
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
    ): UpdateItemResult {
        if (amount <= 0) {
            items.remove(productId)
        } else {
            items[productId] = amount
        }
        return UpdateItemResult.Success(getCart())
    }
}
