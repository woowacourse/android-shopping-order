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
        page: Int,
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
        val fromIndex = page * limit
        if (fromIndex >= filtered.size) return ProductsPage(emptyList(), isLast = true)
        val toIndex = minOf(fromIndex + limit, filtered.size)
        return ProductsPage(
            products = filtered.subList(page, toIndex),
            isLast = toIndex >= filtered.size,
        )
    }

    override suspend fun getProductById(id: Long): Product = products.first { it.id == id }
}
