package woowacourse.shopping.fixture.fake

import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

class FakeCartRepository(initCartItems: List<CartWithProduct> = cartItems) : CartRepository {
    val cartStubs: MutableList<CartWithProduct> = initCartItems.toMutableList()

    override suspend fun getCartItem(productId: Long): Result<CartWithProduct, DataError> {
        val cartItem = cartStubs.firstOrNull { it.product.id == productId } ?: return Result.Error(
            DataError.NotFound
        )
        return Result.Success(cartItem)
    }

    override suspend fun getAllCartItems(): Result<List<CartWithProduct>, DataError> =
        Result.Success(cartStubs)


    override suspend fun postCartItems(productId: Long, quantity: Int): Result<Unit, DataError> {
        val product = FakeProductRepository.productStubs.firstOrNull { it.id == productId }
            ?: return Result.Error(DataError.NotFound)
        val cart = cartStubs.firstOrNull { it.product.id == productId }
        if (cart != null) {
            val index = cartStubs.indexOfFirst { it.product.id == productId }
            cartStubs[index] = CartWithProduct(cart.id, product, Quantity(quantity))
        } else {
            cartStubs.add(CartWithProduct(cartStubs.size.toLong(), product, Quantity(quantity)))
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteCartItem(id: Long): Result<Unit, DataError> = Result.Success(Unit)

    override suspend fun getCartItemsCount(): Result<Int, DataError> =
        Result.Success(cartStubs.size)

    override suspend fun patchCartItem(id: Long, quantity: Int): Result<Unit, DataError> {
        val cart = cartStubs.firstOrNull { it.id == id } ?: return Result.Error(DataError.UNKNOWN)
        val index = cartStubs.indexOf(cart)
        cartStubs[index] = CartWithProduct(id, cart.product, Quantity(quantity))
        return Result.Success(Unit)
    }

    override suspend fun addProductToCart(productId: Long, quantity: Int): Result<Unit, DataError> =
        Result.Success(Unit)

    companion object {
        val cartItems = (1..7).toList().map { id ->
            CartWithProduct(
                id.toLong(), FakeProductRepository.productStubs.first { it.id == id.toLong() },
                Quantity(id)
            )
        }
    }
}
