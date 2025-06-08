package woowacourse.shopping.data.product.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.product.dto.ProductsResponse
import woowacourse.shopping.data.product.dto.ProductsResponse.Content
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity
import woowacourse.shopping.data.product.source.ProductsDataSource
import woowacourse.shopping.data.product.source.RecentViewedProductsDataSource
import woowacourse.shopping.di.DataSourceModule
import woowacourse.shopping.domain.product.PagedProducts
import woowacourse.shopping.domain.product.Product
import java.time.LocalDateTime

class DefaultProductsRepository(
    private val productsDataSource: ProductsDataSource = DataSourceModule.remoteProductsDataSource,
    private val recentViewedProductsDataSource: RecentViewedProductsDataSource = DataSourceModule.localRecentViewedProductsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ProductsRepository {
    override suspend fun loadPagedProducts(
        page: Int,
        size: Int,
    ): PagedProducts =
        withContext(ioDispatcher) {
            val productsResponse: ProductsResponse? =
                productsDataSource.pagedProducts(
                    page = page,
                    size = size,
                )

            PagedProducts.from(
                products =
                    productsResponse?.content?.mapNotNull { it.toDomainOrNull() }
                        ?: emptyList(),
                pageNumber = productsResponse?.pageable?.pageNumber,
                totalPages = productsResponse?.totalPages,
            )
        }

    override suspend fun loadProductsByCategory(category: String): List<Product> =
        withContext(ioDispatcher) {
            val productsEntity: List<ProductEntity>? =
                productsDataSource.getProductsByCategory(category)
            productsEntity?.map { it.toDomain() } ?: emptyList()
        }

    override suspend fun getProductById(id: Long): Product? =
        withContext(ioDispatcher) {
            val productEntity: ProductEntity? = productsDataSource.getProductById(id)
            productEntity?.toDomain()
        }

    override suspend fun loadLatestViewedProduct(): Product? =
        withContext(ioDispatcher) {
            val productId = recentViewedProductsDataSource.load().maxBy { it.viewedAt }.productId
            productsDataSource.getProductById(productId)?.toDomain()
        }

    override suspend fun loadRecentViewedProducts(): List<Product> =
        withContext(ioDispatcher) {
            recentViewedProductsDataSource
                .load()
                .sortedByDescending { it.viewedAt }
                .mapNotNull { recentViewedProductEntity: RecentViewedProductEntity ->
                    productsDataSource
                        .getProductById(recentViewedProductEntity.productId)
                        ?.toDomain()
                }
        }

    override suspend fun addViewedProduct(product: Product) =
        withContext(ioDispatcher) {
            recentViewedProductsDataSource.upsert(
                RecentViewedProductEntity(
                    productId = product.id,
                    viewedAt = LocalDateTime.now(),
                ),
            )
        }

    private fun Content.toDomainOrNull(): Product? {
        return Product(
            id = id ?: return null,
            name = name ?: return null,
            price = price ?: return null,
            category = category ?: return null,
            imageUrl = imageUrl,
        )
    }
}
