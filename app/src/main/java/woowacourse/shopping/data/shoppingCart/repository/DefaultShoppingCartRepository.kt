package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.data.shoppingCart.datasource.ShoppingCartRemoteDataSource
import woowacourse.shopping.data.shoppingCart.mapper.toDomain
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

class DefaultShoppingCartRepository(
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) : ShoppingCartRepository {
    override suspend fun load(
        page: Int,
        size: Int,
    ): Result<ShoppingCarts> =
        runCatching {
            shoppingCartRemoteDataSource.getCartItems(page, size).toDomain()
        }

    override suspend fun add(
        product: Product,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            shoppingCartRemoteDataSource.saveCartItem(
                CartItemRequestDto(
                    productId = product.id,
                    quantity = quantity,
                ),
            )
        }

    override suspend fun increaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
    ): Result<Unit> = updateShoppingCartQuantity(quantity, shoppingCartId)

    override suspend fun decreaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
    ): Result<Unit> {
        if (quantity == 0) return remove(shoppingCartId)
        return updateShoppingCartQuantity(quantity, shoppingCartId)
    }

    private suspend fun updateShoppingCartQuantity(
        quantity: Int,
        shoppingCartId: Long,
    ): Result<Unit> =
        runCatching {
            val requestDto = CartItemQuantityRequestDto(quantity = quantity)
            shoppingCartRemoteDataSource.updateCartItemQuantity(
                shoppingCartId,
                requestDto,
            )
        }

    override suspend fun remove(shoppingCartId: Long): Result<Unit> =
        runCatching {
            shoppingCartRemoteDataSource.deleteCartItem(shoppingCartId)
        }

    override suspend fun fetchAllQuantity(): Result<Int> =
        runCatching {
            shoppingCartRemoteDataSource.getCartCounts().quantity
        }

    companion object {
        private var instance: ShoppingCartRepository? = null

        fun initialize(shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource) {
            if (instance == null) {
                instance =
                    DefaultShoppingCartRepository(shoppingCartRemoteDataSource = shoppingCartRemoteDataSource)
            }
        }

        fun get(): ShoppingCartRepository = instance ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
