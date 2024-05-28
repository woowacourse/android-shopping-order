package woowacourse.shopping.data.product.remote.mock

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class MockWebServerProductRepository(private val productServer: MockWebProductServer) :
    ProductRepository {
    override fun find(id: Int): Product {
        var product: Product? = null
        thread {
            product = productServer.findOrNull(id)
        }.join()
        return product ?: throw IllegalArgumentException(INVALID_ID_MESSAGE)
    }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): List<Product> {
        var products: List<Product> = emptyList()
        thread {
            products = productServer.findRange(page, pageSize)
        }.join()
        return products
    }

    override fun totalProductCount(): Int {
        var totalProductCount: Int = 0
        thread {
            totalProductCount = productServer.totalCount()
        }.join()
        return totalProductCount
    }

    companion object {
        private const val INVALID_ID_MESSAGE = "해당하는 id의 상품이 존재하지 않습니다."
    }
}
