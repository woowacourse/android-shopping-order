package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.safeApiCall
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : ProductRepository {
    override suspend fun fetchProduct(id: Long): Result<Product> =
        safeApiCall {
            productRemoteDataSource.fetchProduct(id).toDomain()
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<Product>> =
        safeApiCall {
            val response = productRemoteDataSource.fetchProducts(null, page, size)
            val products = response.content.map { it.toDomain() }
            val hasMore = !response.last
            PageableItem(products, hasMore)
        }

    override suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>> =
        safeApiCall {
            val category = recentProductLocalDataSource.getRecentViewedProductCategory()
            val response = productRemoteDataSource.fetchProducts(category, null, null)
            val allProducts = response.content
            val filteredProducts = allProducts.filterNot { excludedProductIds.contains(it.id) }
            filteredProducts.take(limit).map { it.toDomain() }
        }
}
