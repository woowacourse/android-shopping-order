package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository(
    initialProducts: List<ProductResponse> = productsFixture,
    initialRecentProductIds: List<Long> = emptyList(),
    private val recentProductSavedLimit: Int = 10,
) : ProductRepository {
    private val products = initialProducts.toList()
    private val recentProductIds = initialRecentProductIds.toMutableList()

    override suspend fun fetchProduct(id: Long): Result<Product> {
        val product = products.find { it.id == id }?.toDomain()
        if (product != null) {
            return Result.success(product)
        }

        return Result.failure(NoSuchElementException("Product not found: $id"))
    }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<Product>> {
        val offset = page * size
        val pagedItems = products.drop(offset).take(size).map { it.toDomain() }
        val hasMore = (offset + size) < productsFixture.size
        val pageableItem = PageableItem(pagedItems, hasMore)
        return Result.success(pageableItem)
    }

    override suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>> {
        val additionalCount = limit + excludedProductIds.size
        val category = products.first().category
        val products =
            products
                .filter { it.category == category }
                .take(additionalCount)
                .map { it.toDomain() }

        return Result.success(products)
    }

    override suspend fun getRecentProducts(limit: Int): Result<List<Product>> {
        val products =
            recentProductIds
                .take(limit)
                .mapNotNull { id -> products.find { it.id == id }?.toDomain() }
        return Result.success(products)
    }

    override suspend fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        recentProductLimit: Int,
    ): Result<Unit> {
        recentProductIds.remove(productId)
        recentProductIds.add(0, productId)

        if (recentProductIds.size > recentProductSavedLimit) {
            recentProductIds.subList(recentProductSavedLimit, recentProductIds.size).clear()
        }

        return Result.success(Unit)
    }
}
