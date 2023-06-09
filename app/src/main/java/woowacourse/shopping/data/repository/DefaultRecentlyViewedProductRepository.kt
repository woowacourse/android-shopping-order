package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.datasource.RecentlyViewedProductDataSource
import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

class DefaultRecentlyViewedProductRepository(
    private val recentlyViewedProductDataSource: RecentlyViewedProductDataSource,
    private val productDataSource: ProductDataSource
) : RecentlyViewedProductRepository {

    override fun save(
        product: Product,
        viewedTime: LocalDateTime
    ): CompletableFuture<Result<RecentlyViewedProduct>> {
        return CompletableFuture.supplyAsync {
            recentlyViewedProductDataSource.save(product, viewedTime)
        }
    }

    override fun findLimitedOrderByViewedTimeDesc(limit: Int): CompletableFuture<Result<List<RecentlyViewedProduct>>> {
        return CompletableFuture.supplyAsync {
            productDataSource.findAll().mapCatching { products ->
                findLimitedRecentlyViewedProduct(products, limit).getOrThrow()
            }
        }
    }

    private fun findLimitedRecentlyViewedProduct(
        products: List<ProductEntity>,
        limit: Int
    ): Result<List<RecentlyViewedProduct>> {
        return recentlyViewedProductDataSource.findLimitedOrderByViewedTimeDesc(limit)
            .mapCatching { recentlyViewedProductEntities ->
                processRecentProduct(recentlyViewedProductEntities, products)
            }
    }

    private fun processRecentProduct(
        recentlyViewedProductEntities: List<RecentlyViewedProductEntity>,
        products: List<ProductEntity>
    ): List<RecentlyViewedProduct> {
        return recentlyViewedProductEntities.mapNotNull { entity ->
            val productMap = products.associateBy { it.id }
            val product = productMap[entity.productId]?.toDomain() ?: return@mapNotNull null
            RecentlyViewedProduct(
                product.id, product, LocalDateTime.parse(entity.viewedDateTime)
            )
        }
    }
}
