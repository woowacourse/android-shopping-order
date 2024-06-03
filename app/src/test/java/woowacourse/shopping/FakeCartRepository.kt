package woowacourse.shopping

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository

class FakeCartRepository(savedCartItemEntities: List<CartItem> = emptyList()) : CartRepository {
    private val cart: MutableList<CartItem> = savedCartItemEntities.toMutableList()
    private var id: Int = 0

    override fun findByProductId(
        productId: Int,
        callback: (Result<CartItem?>) -> Unit,
    ) {
        val cartItem = cart.find { it.productId == productId }
        callback(Result.success(cartItem))
    }

    override fun syncFindByProductId(
        productId: Int,
        totalItemCount: Int,
    ): CartItem? {
        return cart.find { it.productId == productId }
    }

    override fun findAll(callback: (Result<List<CartItem>>) -> Unit) {
        callback(Result.success(cart))
    }

    override fun delete(
        id: Int,
        callback: (Result<Unit>) -> Unit,
    ) {
        cart.removeIf { it.id == id }
        callback(Result.success(Unit))
    }

    override fun add(
        productId: Int,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    ) {
        val cartItem = CartItem(id++, productId, quantity)
        cart.add(cartItem)
        callback(Result.success(Unit))
    }

    override fun changeQuantity(
        id: Int,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    ) {
        val position = cart.indexOfFirst { it.id == id }
        cart[position] = cart[position].copy(quantity = quantity)
        callback(Result.success(Unit))
    }

    override fun getTotalQuantity(callback: (Result<Int>) -> Unit) {
        val totalQuantity = cart.fold(0) { acc, cartItem -> acc + cartItem.quantity.count }
        callback(Result.success(totalQuantity))
    }

    override fun syncGetTotalQuantity(): Int {
        return cart.fold(0) { acc, cartItem -> acc + cartItem.quantity.count }
    }
}
