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

    override fun save(
        product: Product,
        viewedTime: LocalDateTime,
        onFinish: (Result<RecentlyViewedProduct>) -> Unit
    ) {
        recentlyViewedProductDataSource.save(product, viewedTime, onFinish)
    }

    override fun findFirst10OrderByViewedTimeDesc(onFinish: (Result<List<RecentlyViewedProduct>>) -> Unit) {
        productDataSource.findAll { productResult ->
            productResult.onSuccess { products ->
                findFirst10RecentlyViewedProduct(onFinish, products)
            }.onFailure {
                onFinish(Result.failure(it))
            }
        }
    }

    private fun findFirst10RecentlyViewedProduct(
        onFinish: (Result<List<RecentlyViewedProduct>>) -> Unit,
        products: List<ProductEntity>
    ) {
        recentlyViewedProductDataSource.findFirst10OrderByViewedTimeDesc { recentlyViewedProductResult ->
            recentlyViewedProductResult.onSuccess { recentlyViewedProductEntities ->
                processRecentProduct(onFinish, recentlyViewedProductEntities, products)
            }.onFailure {
                onFinish(Result.failure(it))
            }
        }
    }

    private fun processRecentProduct(
        onFinish: (Result<List<RecentlyViewedProduct>>) -> Unit,
        recentlyViewedProductEntities: List<RecentlyViewedProductEntity>,
        products: List<ProductEntity>
    ) {
        onFinish(
            Result.success(
                recentlyViewedProductEntities.mapNotNull { entity ->
                    val productMap = products.associateBy { it.id }
                    val product = productMap[entity.productId]?.toDomain() ?: return@mapNotNull null
                    RecentlyViewedProduct(
                        product.id, product, LocalDateTime.parse(entity.viewedDateTime)
                    )
                }
            )
        )
    }
}
