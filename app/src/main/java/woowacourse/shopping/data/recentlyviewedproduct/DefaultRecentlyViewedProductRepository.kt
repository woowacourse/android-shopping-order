package woowacourse.shopping.data.recentlyviewedproduct

import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import java.time.LocalDateTime

class DefaultRecentlyViewedProductRepository(
    private val recentlyViewedProductDataSource: RecentlyViewedProductDataSource,
    private val productDataSource: ProductDataSource
) : RecentlyViewedProductRepository {

    override fun save(product: Product, viewedTime: LocalDateTime): Result<RecentlyViewedProduct> {
        return recentlyViewedProductDataSource.save(product, viewedTime)
    }

    override fun findFirst10OrderByViewedTimeDesc(): Result<List<RecentlyViewedProduct>> {
        return productDataSource.findAll().mapCatching { products ->
            findFirst10RecentlyViewedProduct(products).getOrThrow()
        }
    }

    private fun findFirst10RecentlyViewedProduct(
        products: List<ProductEntity>
    ): Result<List<RecentlyViewedProduct>> {
        return recentlyViewedProductDataSource.findFirst10OrderByViewedTimeDesc()
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
