package woowacourse.shopping.data.database.recentlyviewedproduct

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.datasource.recentlyviewedproduct.RecentlyViewedProductDataSource
import woowacourse.shopping.domain.RecentlyViewedProduct
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import java.time.LocalDateTime

class RecentlyViewedProductRepositoryImpl(
    private val recentlyViewedProductDataSource: RecentlyViewedProductDataSource,
    private val productDataSource: ProductDataSource
) : RecentlyViewedProductRepository {

    override fun save(recentlyViewedProduct: RecentlyViewedProduct) {
        recentlyViewedProductDataSource.save(recentlyViewedProduct)
    }

    override fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProduct>) -> Unit) {
        recentlyViewedProductDataSource.findFirst10OrderByViewedTimeDesc { recentlyViewedProductEntities ->
            val recentlyViewedProductEntitiesMap =
                recentlyViewedProductEntities.associateBy { it.productId }
            productDataSource.findAll { products ->
                onFinish(
                    products.mapNotNull { product ->
                        if (recentlyViewedProductEntitiesMap[product.id] != null) {
                            RecentlyViewedProduct(
                                product,
                                LocalDateTime.parse(recentlyViewedProductEntitiesMap[product.id]?.viewedDateTime)
                            ).apply { id = product.id }
                        } else {
                            null
                        }
                    }
                )
            }
        }
    }
}
