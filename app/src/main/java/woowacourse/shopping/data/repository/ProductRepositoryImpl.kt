package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : ProductRepository {
    override fun fetchProduct(
        id: Long,
        onResult: (Result<Product>) -> Unit,
    ) = runCatchingInThread(onResult) {
        productRemoteDataSource.fetchProduct(id).getOrThrow().toDomain()
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<Product>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val response = productRemoteDataSource.fetchProducts(null, page, size).getOrThrow()
        val products = response.content.map { it.toDomain() }
        val hasMore = !response.last
        PageableItem(products, hasMore)
    }

    override fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
        onResult: (Result<List<Product>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val category = recentProductLocalDataSource.getRecentViewedProductCategory()

        val fetchLimit = limit + excludedProductIds.size
        val response = productRemoteDataSource.fetchProducts(category, 0, fetchLimit).getOrNull()
        val allProducts = response?.content ?: emptyList()

        val filteredProducts =
            allProducts
                .filterNot { excludedProductIds.contains(it.id) }
                .take(limit)

        filteredProducts.map { it.toDomain() }
    }
}
