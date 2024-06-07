package woowacourse.shopping.data.order

import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.common.ResponseHandlingUtils.handleResponse
import woowacourse.shopping.data.history.ProductHistoryDataSource
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
    private val productDataSource: ProductDataSource,
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : OrderRepository {
    override suspend fun orderCartItems(cartItemIds: List<Long>) {
        handleResponse(orderDataSource.orderCartItems(cartItemIds))
    }

    override suspend fun loadRecommendedProducts(): List<Product> {
        val productId: Long = productHistoryDataSource.fetchLatestProduct()
        val category: String = handleResponse(productDataSource.loadById(productId)).category
        val cartItemsProductDto: List<ProductDto> = handleResponse(cartItemDataSource.fetchCartItems()).content.map { it.product }

        return handleResponse(productDataSource.loadByCategory(category)).content.filterNot { cartItemsProductDto.contains(it) }
            .map { productDto -> productDto.toDomain() }.take(10)
    }
}
