package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity
import woowacourse.shopping.data.product.source.LocalRecentViewedProductsDataSource
import woowacourse.shopping.data.product.source.ProductsDataSource
import woowacourse.shopping.data.product.source.RecentViewedProductsDataSource
import woowacourse.shopping.data.product.source.RemoteProductsDataSource
import woowacourse.shopping.domain.product.PageableProducts
import woowacourse.shopping.domain.product.Product
import java.time.LocalDateTime
import kotlin.concurrent.thread

class DefaultProductsRepository(
    private val productsDataSource: ProductsDataSource = RemoteProductsDataSource(),
    private val recentViewedProductsDataSource: RecentViewedProductsDataSource = LocalRecentViewedProductsDataSource,
) : ProductsRepository {
    override fun loadPageableProducts(
        page: Int,
        size: Int,
        onLoad: (Result<PageableProducts>) -> Unit,
    ) {
        {
            val pageableProductData: PageableProductData =
                productsDataSource.pageableProducts(
                    page = page,
                    size = size,
                )
            PageableProducts(
                products = pageableProductData.products.map { it.toDomain() },
                loadable = pageableProductData.loadable,
            )
        }.runAsync(onLoad)
    }

    override fun loadProductsByCategory(
        category: String,
        onLoad: (Result<List<Product>>) -> Unit,
    ) {
        {
            val productsEntity: List<ProductEntity>? =
                productsDataSource.getProductsByCategory(category)
            productsEntity?.map { it.toDomain() } ?: emptyList()
        }.runAsync(onLoad)
    }

    override fun getProductById(
        id: Long,
        onLoad: (Result<Product?>) -> Unit,
    ) {
        {
            val productEntity: ProductEntity? = productsDataSource.getProductById(id)
            productEntity?.toDomain()
        }.runAsync(onLoad)
    }

    override fun loadLatestViewedProduct(onLoad: (productId: Result<Product?>) -> Unit) {
        {
            val productId = recentViewedProductsDataSource.load().maxBy { it.viewedAt }.productId
            productsDataSource.getProductById(productId)?.toDomain()
        }.runAsync(onLoad)
    }

    override fun loadRecentViewedProducts(onLoad: (Result<List<Product>>) -> Unit) {
        {
            recentViewedProductsDataSource
                .load()
                .sortedByDescending { it.viewedAt }
                .mapNotNull { recentViewedProductEntity: RecentViewedProductEntity ->
                    productsDataSource
                        .getProductById(recentViewedProductEntity.productId)
                        ?.toDomain()
                }
        }.runAsync(onLoad)
    }

    override fun addViewedProduct(
        product: Product,
        onLoad: (Result<Unit>) -> Unit,
    ) {
        {
            recentViewedProductsDataSource.upsert(
                RecentViewedProductEntity(
                    productId = product.id,
                    viewedAt = LocalDateTime.now(),
                ),
            )
        }.runAsync(onLoad)
    }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
