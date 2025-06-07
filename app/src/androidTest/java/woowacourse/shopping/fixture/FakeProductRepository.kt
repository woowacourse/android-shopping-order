package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override suspend fun fetchProduct(id: Long): Result<Product> {
        val product = productsFixture.find { it.id == id }?.toDomain()
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
        val pagedItems = productsFixture.drop(offset).take(size).map { it.toDomain() }
        val hasMore = (offset + size) < productsFixture.size
        val pageableItem = PageableItem(pagedItems, hasMore)
        return Result.success(pageableItem)
    }

    override suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>> {
        val additionalCount = limit + excludedProductIds.size
        val category = productsFixture.first().category
        val products =
            productsFixture
                .filter { it.category == category }
                .take(additionalCount)
                .map { it.toDomain() }

        return Result.success(products)
    }
}
