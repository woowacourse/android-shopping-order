package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.data.shoppingCart.datasource.ShoppingCartRemoteDataSource
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

class DefaultShoppingCartRepository(
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) : ShoppingCartRepository {
    override fun load(
        page: Int,
        size: Int,
        onResult: (Result<ShoppingCarts>) -> Unit,
    ) {
        shoppingCartRemoteDataSource.getCartItems(page, size) { result ->
            onResult(result.mapCatching { it?.toDomain() ?: ShoppingCarts(false, emptyList()) })
        }
    }

    override fun add(
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        shoppingCartRemoteDataSource.saveCartItem(
            CartItemRequestDto(
                productId = product.id,
                quantity = quantity,
            ),
        ) { result ->
            onResult(result.mapCatching { it })
        }
    }

    override fun increaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        updateShoppingCartQuantity(quantity, shoppingCartId, onResult)
    }

    override fun decreaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        if (quantity == 0) return remove(shoppingCartId, onResult)
        updateShoppingCartQuantity(quantity, shoppingCartId, onResult)
    }

    private fun updateShoppingCartQuantity(
        quantity: Int,
        shoppingCartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val requestDto = CartItemQuantityRequestDto(quantity = quantity)
        shoppingCartRemoteDataSource.updateCartItemQuantity(shoppingCartId, requestDto) { result ->
            onResult(result.mapCatching { it })
        }
    }

    override fun remove(
        shoppingCartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        shoppingCartRemoteDataSource.deleteCartItem(shoppingCartId) { result ->
            onResult(result.mapCatching { it })
        }
    }

    override fun fetchAllQuantity(onResult: (Result<Int>) -> Unit) {
        shoppingCartRemoteDataSource.getCartCounts { result ->
            onResult(result.mapCatching { it?.quantity ?: 0 })
        }
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
