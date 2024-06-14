package woowacourse.shopping.data.order.remote

import woowacourse.shopping.data.cart.remote.datasource.CartItemDataSource
import woowacourse.shopping.data.common.ApiResponseHandler.handleResponse
import woowacourse.shopping.data.common.ApiResponseHandler.handleResponseResult
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.history.local.datasource.ProductHistoryDataSource
import woowacourse.shopping.data.order.remote.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.product.remote.datasource.ProductDataSource
import woowacourse.shopping.data.product.remote.dto.ProductDto
import woowacourse.shopping.data.product.remote.dto.ProductDto.Companion.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.order.OrderRepository

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
    private val productDataSource: ProductDataSource,
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : OrderRepository {
    override suspend fun orderCartItems(cartItemIds: List<Long>): ResponseResult<Unit> =
        handleResponseResult(orderDataSource.orderCartItems(cartItemIds)) { ResponseResult.Success(Unit) }

    override suspend fun loadRecommendedProducts(): ResponseResult<List<Product>> {
        val category: String = handleResponse(productDataSource.loadById(getProductId())).category
        val cartItemsProductDto: List<ProductDto> =
            handleResponse(cartItemDataSource.fetchCartItems()).content.map { it.product }

        return handleResponseResult(productDataSource.loadByCategory(category)) { response ->
            val recommendedProducts =
                response.content.filterNot { cartItemsProductDto.contains(it) }
                    .map { productDto -> productDto.toDomain() }.take(RECOMMENDED_PRODUCTS_COUNT)
            ResponseResult.Success(recommendedProducts)
        }
    }

    private suspend fun getProductId(): Long {
        var productId: Long = 0
        productHistoryDataSource.fetchLatestProduct()
            .onSuccess {
                productId = it
            }.onFailure {
                throw IllegalStateException(it.message)
            }
        return productId
    }

    companion object {
        private const val RECOMMENDED_PRODUCTS_COUNT = 10
    }
}
