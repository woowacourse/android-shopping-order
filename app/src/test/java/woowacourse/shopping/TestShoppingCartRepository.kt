package woowacourse.shopping

import woowacourse.shopping.TestFixture.cartItem0
import woowacourse.shopping.TestFixture.cartItem1
import woowacourse.shopping.TestFixture.cartItem2
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class TestShoppingCartRepository : ShoppingCartRepository {
    private val cartItems =
        mutableListOf(cartItem0, cartItem1, cartItem2)

    override suspend fun insertCartItem(product: Product): Result<Unit> {
        cartItems.add(CartItem(cartItems.size.toLong(), product))
        return Result.success(Unit)
    }

    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return Result.success(cartItems)
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        val cartItemResult =
            cartItems.find { it.product.id == productId }?.let {
                CartItemResult(it.id, it.product.cartItemCounter)
            } ?: return Result.failure(Exception("정보 없음"))
        return Result.success(cartItemResult)
    }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun increaseCartItem(product: Product): Result<Unit> {
        cartItems.filter { it.product.id == product.id }.forEach { it.product.cartItemCounter.increase() }
        return Result.success(Unit)
    }

    override suspend fun decreaseCartItem(product: Product): Result<Unit> {
        cartItems.filter { it.product.id == product.id }.forEach { it.product.cartItemCounter.decrease() }
        return Result.success(Unit)
    }

    override suspend fun updateCartCount(cartItemResult: CartItemResult): Result<Unit> {
        cartItems.filter { it.id == cartItemResult.cartItemId }.forEach {
            it.product.cartItemCounter.updateCount(
                cartItemResult.counter.itemCount,
            )
        }
        return Result.success(Unit)
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return Result.success(cartItems.size)
    }
}
