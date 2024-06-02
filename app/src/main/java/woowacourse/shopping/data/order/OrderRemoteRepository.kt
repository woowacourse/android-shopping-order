package woowacourse.shopping.data.order

import woowacourse.shopping.data.history.ProductHistoryDataSource
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.repository.order.OrderRepository

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
    private val productDataSource: ProductDataSource,
    private val productHistoryDataSource: ProductHistoryDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderDataSource.order(cartItemIds)
    }

    override fun recommendedProducts(): List<ProductData> {
        val productId: Long = productHistoryDataSource.loadLatestProduct()
        return productDataSource.findByCategory(productId)
    }
}
