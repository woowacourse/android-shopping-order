package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.network.MockingServer
import woowacourse.shopping.data.network.response.ProductEntity
import woowacourse.shopping.data.network.response.ProductPageEntity

class ProductsDataSource(private val service: MockingServer) {
    fun getProduct(productId: Long): ProductEntity = service.getProduct(productId)

    fun getProducts(productIds: List<Long>): List<ProductEntity> {
        return service.getProducts(productIds)
    }

    fun singlePage(
        fromIndex: Int,
        toIndex: Int,
    ): ProductPageEntity = service.singlePage(fromIndex, toIndex)
}
