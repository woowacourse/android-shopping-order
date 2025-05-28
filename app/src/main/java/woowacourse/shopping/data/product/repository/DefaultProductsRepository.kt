package woowacourse.shopping.data.product.repository

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
    override fun load(
        page: Int,
        size: Int,
        onLoad: (Result<PageableProducts>) -> Unit,
    ) {
        {
            val pageableProducts = productsDataSource.pageableProducts(page, size)
            val products = pageableProducts.products.map(ProductEntity::toDomain)
            val hasNext = pageableProducts.loadable

            PageableProducts(products, hasNext)
        }.runAsync(onLoad)
    }

    override fun loadProductById(
        id: Long,
        onLoad: (Result<Product?>) -> Unit,
    ) {
        { productsDataSource.getProductById(id)?.toDomain() }.runAsync(onLoad)
    }

    override fun loadLatestViewedProduct(onLoad: (Result<Product?>) -> Unit) {
        {
            val latestViewedProductId: Long? =
                recentViewedProductsDataSource
                    .load()
                    .maxByOrNull { it.viewedAt }
                    ?.productId
            if (latestViewedProductId == null) {
                null
            } else {
                productsDataSource.getProductById(latestViewedProductId)?.toDomain()
            }
        }.runAsync(onLoad)
    }

    override fun loadLastViewedProducts(onLoad: (Result<List<Product>>) -> Unit) {
        {
            recentViewedProductsDataSource
                .load()
                .sortedByDescending { it.viewedAt }
                .mapNotNull { productsDataSource.getProductById(it.productId)?.toDomain() }
        }.runAsync(onLoad)
    }

    override fun recordViewedProduct(
        product: Product,
        onLoad: (Result<Unit>) -> Unit,
    ) {
        {
            recentViewedProductsDataSource.upsert(
                RecentViewedProductEntity(product.id, LocalDateTime.now()),
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
