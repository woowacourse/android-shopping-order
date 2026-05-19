package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductPage

class FakeProductRepository(
    initial: List<Product> = emptyList(),
) : ProductRepository {

    private val products: List<Product> = initial

    override suspend fun loadProducts(
        page: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): ProductPage {
        val filtered = if (category == null) products else products.filter { it.category == category }
        val start = page * pageSize
        if (start >= filtered.size) return ProductPage(emptyList(), isLast = true)
        val end = minOf(start + pageSize, filtered.size)

        return ProductPage(
            products = filtered.subList(start, end),
            isLast = end >= filtered.size,
        )
    }

    override suspend fun getProduct(id: String): Product = products.first { it.id == id }
}
