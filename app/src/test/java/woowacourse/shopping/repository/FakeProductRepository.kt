package woowacourse.shopping.repository

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.query.ProductPageResult

class FakeProductRepository(
    products: List<Product>,
) : ProductRepository {
    private val products = products.toList()
    private val productMap: Map<Long, Product> = this.products.associateBy { it.id }

    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductPageResult {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(0)
        if (safeSize == 0) {
            return ProductPageResult(
                items = emptyList(),
                totalElements = products.size,
                page = safePage,
                size = safeSize,
                hasNext = false,
            )
        }
        val fromIndex = (safePage * safeSize).coerceIn(0, products.size)
        val toIndex = minOf(fromIndex + safeSize, products.size)

        return ProductPageResult(
            items = products.subList(fromIndex, toIndex),
            totalElements = products.size,
            page = safePage,
            size = safeSize,
            hasNext = toIndex < products.size,
        )
    }

    override suspend fun getProductsByCategory(
        category: String,
        page: Int,
        size: Int,
    ): ProductPageResult {
        val filteredProducts = products.filter { it.category == category }
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(0)
        if (safeSize == 0) {
            return ProductPageResult(
                items = emptyList(),
                totalElements = filteredProducts.size,
                page = safePage,
                size = safeSize,
                hasNext = false,
            )
        }
        val fromIndex = (safePage * safeSize).coerceIn(0, filteredProducts.size)
        val toIndex = minOf(fromIndex + safeSize, filteredProducts.size)

        return ProductPageResult(
            items = filteredProducts.subList(fromIndex, toIndex),
            totalElements = filteredProducts.size,
            page = safePage,
            size = safeSize,
            hasNext = toIndex < filteredProducts.size,
        )
    }

    override suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product> =
        ids
            .mapNotNull { id ->
                productMap[id]?.let { id to it }
            }.toMap()
}
