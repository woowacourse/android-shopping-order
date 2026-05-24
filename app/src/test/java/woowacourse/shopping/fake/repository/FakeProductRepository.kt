package woowacourse.shopping.fake.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsPage
import woowacourse.shopping.domain.repository.ProductRepository
import java.io.IOException

class FakeProductRepository(
    private val products: List<Product>,
    var shouldFail: Boolean = false,
) : ProductRepository {
    override suspend fun getProducts(
        offset: Int,
        limit: Int,
        category: String?,
    ): ProductsPage {
        if (shouldFail) throw IOException()
        val filtered =
            if (category == null) {
                products
            } else {
                products.filter { it.category == category }
            }
        if (offset >= filtered.size) return ProductsPage(emptyList(), isLast = true)
        val toIndex = minOf(offset + limit, filtered.size)
        return ProductsPage(
            products = filtered.subList(offset, toIndex),
            isLast = toIndex >= filtered.size,
        )
    }

    override suspend fun getProductById(id: Long): Product = products.first { it.id == id }
}
