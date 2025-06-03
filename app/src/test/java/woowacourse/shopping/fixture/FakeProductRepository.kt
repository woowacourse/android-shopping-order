package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override fun fetchProduct(
        id: Long,
        onResult: (Result<Product>) -> Unit,
    ) {
        val product = productsFixture.find { it.id == id }?.toDomain()
        if (product != null) {
            onResult(Result.success(product))
            return
        }

        onResult(Result.failure(NoSuchElementException("Product not found: $id")))
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<Product>>) -> Unit,
    ) {
        val offset = page * size
        val pagedItems = productsFixture.drop(offset).take(size).map { it.toDomain() }
        val hasMore = (offset + size) < productsFixture.size
        val pageableItem = PageableItem(pagedItems, hasMore)
        onResult(Result.success(pageableItem))
    }

    override fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        val additionalCount = limit + excludedProductIds.size
        val category = productsFixture.first().category
        val products =
            productsFixture
                .filter { it.category == category }
                .take(additionalCount)
                .map { it.toDomain() }

        onResult(Result.success(products))
    }
}
