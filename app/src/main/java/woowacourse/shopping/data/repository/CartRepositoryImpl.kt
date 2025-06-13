package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.di.ApiResult
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
        when (val apiResult = dataSource.getCartItems(page, size)) {
            is ApiResult.Success ->
                runCatching {
                    val response = apiResult.data
                    val items = response.content.map { it.toDomain() }
                    val pageInfo = Page(page, response.first, response.last)
                    Products(items, pageInfo)
                }

            is ApiResult.ClientError -> Result.failure(Exception("Client error: ${apiResult.code} ${apiResult.message}"))
            is ApiResult.ServerError -> Result.failure(Exception("Server error: ${apiResult.code} ${apiResult.message}"))
            is ApiResult.NetworkError -> Result.failure(apiResult.throwable)
            ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
        }

    override suspend fun fetchAllCartProducts(): Result<Products> {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE

        return fetchCartProducts(firstPage, maxSize)
    }

    override suspend fun fetchCartItemCount(): Result<Int> =
        when (val apiResult = dataSource.getCartItemsCount()) {
            is ApiResult.Success -> runCatching { apiResult.data.quantity }
            is ApiResult.ClientError -> Result.failure(Exception("Client error: ${apiResult.code} ${apiResult.message}"))
            is ApiResult.ServerError -> Result.failure(Exception("Server error: ${apiResult.code} ${apiResult.message}"))
            is ApiResult.NetworkError -> Result.failure(apiResult.throwable)
            ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
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
