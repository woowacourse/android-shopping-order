package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.remote.datasource.CartDataSourceImpl
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class CartRepositoryImpl(
    private val cartDataSourceImpl: CartDataSourceImpl,
) : CartRepository {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<CartItem>> {
        var result: Result<List<CartItem>>? = null
        thread {
            cartDataSourceImpl.getCartItems(page, size, sort)
                .onSuccess {
                    val cartItems = it.map { cartItemDto -> cartItemDto.toCartItem() }
                    result = Result.success(cartItems)
                }.onFailure {
                    result = Result.failure(IllegalArgumentException())
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            val cartItemRequest = CartItemRequest(productId, quantity)
            cartDataSourceImpl.addCartItem(cartItemRequest)
                .onSuccess { result = Result.success(Unit) }
                .onFailure { result = Result.failure(IllegalArgumentException()) }
        }.join()
        return result ?: throw Exception()
    }

    override fun deleteCartItem(cartItemId: Int): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            cartDataSourceImpl.deleteCartItem(cartItemId)
                .onSuccess { result = Result.success(Unit) }
                .onFailure { result = Result.failure(IllegalArgumentException()) }
        }.join()

        return result ?: throw Exception()
    }

    override fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            val cartQuantityDto = CartQuantityDto(quantity)
            cartDataSourceImpl.updateCartItem(cartItemId, cartQuantityDto)
                .onSuccess { result = Result.success(Unit) }
                .onFailure { result = Result.failure(IllegalArgumentException()) }
        }.join()

        return result ?: throw Exception()
    }

    override fun getCartTotalQuantity(): Result<Int> {
        var result: Result<Int>? = null
        thread {
            cartDataSourceImpl.getCartTotalQuantity()
                .onSuccess { result = Result.success(it) }
                .onFailure { result = Result.failure(IllegalArgumentException()) }
        }.join()

        return result ?: throw Exception()
    }
}
