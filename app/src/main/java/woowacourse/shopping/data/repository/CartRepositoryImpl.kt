package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val dataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<Products> =
        runCatching {
            val response = dataSource.getCartItems(page, size)
            val items = response.content.map { it.toDomain() }

            val isFirst = response.first
            val isLast = response.last
            val pageInfo = Page(page, isFirst, isLast)

            Products(items, pageInfo)
        }

    override suspend fun fetchAllCartProducts(): Result<Products> {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE

        return fetchCartProducts(firstPage, maxSize)
    }

    override suspend fun fetchCartItemCount(): Result<Int> =
        runCatching {
            val response = dataSource.getCartItemsCount()
            response.quantity
        }

    override suspend fun addCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            dataSource.postCartItems(productId, quantity)
        }

    override suspend fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            dataSource.patchCartItem(cartId, quantity)
        }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> =
        runCatching {
            dataSource.deleteCartItem(cartId)
        }
}
