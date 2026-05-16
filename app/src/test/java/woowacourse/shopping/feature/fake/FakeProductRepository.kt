package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Product

class FakeProductRepository(
    initial: List<Product> = emptyList(),
) : ProductRepository {

    private val products: List<Product> = initial

    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product> {
        val filtered = if (category == null) products else products.filter { it.category == category }
        if (startIndex >= filtered.size) return emptyList()
        val end = minOf(startIndex + pageSize, filtered.size)
        return filtered.subList(startIndex, end)
    }

    override suspend fun getProduct(id: String): Product = products.first { it.id == id }
}
