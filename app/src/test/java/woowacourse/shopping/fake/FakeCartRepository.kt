package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.product

class FakeCartRepository(savedCartItemEntities: List<CartItem> = emptyList()) : CartRepository {
    private val cart: MutableList<CartItem> = savedCartItemEntities.toMutableList()
    private var id: Int = 0

    override suspend fun find(cartItemId: Int): Result<CartItem?> {
        val cartItem = cart.find { it.id == cartItemId }
        return Result.success(cartItem)
    }

    override suspend fun findByProductId(productId: Int): Result<CartItem?> {
        val cartItem = cart.find { it.product.id == productId }
        return Result.success(cartItem)
    }

    override suspend fun findAll(): Result<List<CartItem>> {
        return Result.success(cart)
    }

    override suspend fun delete(id: Int): Result<Unit> {
        cart.removeIf { it.id == id }
        return Result.success(Unit)
    }

    override suspend fun add(
        productId: Int,
        quantity: Quantity,
    ): Result<Unit> {
        val cartItem = CartItem(id++, product(productId), quantity)
        cart.add(cartItem)
        return Result.success(Unit)
    }

    override suspend fun changeQuantity(
        id: Int,
        quantity: Quantity,
    ): Result<Unit> {
        val position = cart.indexOfFirst { it.id == id }
        cart[position] = cart[position].copy(quantity = quantity)
        return Result.success(Unit)
    }

    override suspend fun getTotalQuantity(): Result<Int> {
        val totalQuantity = cart.fold(0) { acc, cartItem -> acc + cartItem.quantity.count }
        return Result.success(totalQuantity)
    }
}
