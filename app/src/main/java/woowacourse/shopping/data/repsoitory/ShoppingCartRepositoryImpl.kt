package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartRepositoryImpl(private val dataSource: ShoppingCartRemoteDataSource) :
    ShoppingCartRepository {
    override suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int> =
        runCatching {
            dataSource.insertCartProduct(
                productId = productId,
                quantity = quantity,
            )
        }

    override suspend fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            dataSource.updateCartProduct(cartId = cartId, quantity = quantity)
        }

    override suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts> =
        runCatching {
            dataSource.getCartProductsPaged(page = page, size = size)
        }

    override suspend fun getCartProductsQuantity(): Result<Int> =
        runCatching {
            dataSource.getCartProductsQuantity()
        }

    override suspend fun deleteCartProductById(cartId: Int): Result<Unit> =
        runCatching {
            dataSource.deleteCartProductById(cartId = cartId)
        }

    override suspend fun getAllCarts(): Result<Carts> {
        val totalElements =
            dataSource.getCartProductsPaged(
                page = ProductRepositoryImpl.FIRST_PAGE,
                size = ProductRepositoryImpl.FIRST_SIZE,
            ).totalElements

        return runCatching {
            dataSource.getCartProductsPaged(
                page = ProductRepositoryImpl.FIRST_PAGE,
                size = totalElements,
            )
        }
    }
}
