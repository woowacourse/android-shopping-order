package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartRepositoryImpl(private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource) :
    ShoppingCartRepository {
    override suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int> =
        runCatching {
            shoppingCartRemoteDataSource.insertCartProduct(
                productId = productId,
                quantity = quantity,
            )
        }

    override suspend fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            shoppingCartRemoteDataSource.updateCartProduct(cartId = cartId, quantity = quantity)
        }

    override suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts> =
        runCatching {
            shoppingCartRemoteDataSource.getCartProductsPaged(page = page, size = size)
        }

    override suspend fun getCartProductsQuantity(): Result<Int> =
        runCatching {
            shoppingCartRemoteDataSource.getCartProductsQuantity()
        }

    override suspend fun deleteCartProductById(cartId: Int): Result<Unit> =
        runCatching {
            shoppingCartRemoteDataSource.deleteCartProductById(cartId = cartId)
        }

    override suspend fun getAllCarts(): Result<Carts> {
        val cartTotalElement = shoppingCartRemoteDataSource.getCartsTotalElement()

        return runCatching {
            shoppingCartRemoteDataSource.getEntireCarts(cartTotalElement)
        }
    }
}
