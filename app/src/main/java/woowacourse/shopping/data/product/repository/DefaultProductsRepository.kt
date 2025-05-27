package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.product.dataSource.LocalRecentViewedProductsDataSource
import woowacourse.shopping.data.product.dataSource.ProductsDataSource
import woowacourse.shopping.data.product.dataSource.RecentViewedProductsDataSource
import woowacourse.shopping.data.product.dataSource.RemoteProductsDataSource
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity
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
            val pageableProducts = productsDataSource.load(page, size)
            val products = pageableProducts.products.map(ProductEntity::toDomain)
            val hasNext = pageableProducts.hasNext

            PageableProducts(products, hasNext)
        }.runAsync(onLoad)
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
                productsDataSource.getById(latestViewedProductId)?.toDomain()
            }
        }.runAsync(onLoad)
    }

    override fun loadLastViewedProducts(onLoad: (Result<List<Product>>) -> Unit) {
        {
            recentViewedProductsDataSource
                .load()
                .sortedByDescending { it.viewedAt }
                .mapNotNull { productsDataSource.getById(it.productId)?.toDomain() }
        }.runAsync(onLoad)
    }

    override fun recordViewedProduct(product: Product) {
        thread {
            recentViewedProductsDataSource.upsert(
                RecentViewedProductEntity(
                    product.id,
                    LocalDateTime.now(),
                ),
            )
        }
    }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
