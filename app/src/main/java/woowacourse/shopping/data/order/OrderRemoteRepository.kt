package woowacourse.shopping.data.order

import woowacourse.shopping.data.history.ProductHistoryDataSource
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
    private val productDataSource: ProductDataSource,
    private val productHistoryDataSource: ProductHistoryDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderDataSource.order(cartItemIds)
    }

    override fun recommendedProducts(): List<Product> {
        val productId: Long = productHistoryDataSource.loadLatestProduct()
        val productsDto = productDataSource.findByCategory(productId)
        return productsDto.map { productDto -> productDto.toDomain() }
    }
}
