package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.product.catalog.ProductUiModel
import kotlin.concurrent.thread

class RecentlyViewedProductRepositoryImpl(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
    private val catalogProductRepository: CatalogProductRepository,
) : RecentlyViewedProductRepository {
    override fun insertRecentlyViewedProductId(productId: Long) {
        thread {
            recentlyViewedProductDao.insertRecentlyViewedProductUid(
                RecentlyViewedProductEntity(productId),
            )
        }
    }

    override fun getRecentlyViewedProducts(callback: (List<CartProductEntity>) -> Unit) {
        thread {
            val productIds = recentlyViewedProductDao.getRecentlyViewedProductIds()
            catalogProductRepository.getCartProductsByIds(productIds) { products ->
                val entities = products.map { it.toEntity() }
                callback(entities)
            }
        }
    }

    override fun getLatestViewedProduct(callback: (ProductUiModel) -> Unit) {
        thread {
            val productId = recentlyViewedProductDao.getLatestViewedProductId()
            catalogProductRepository.getProduct(productId, onSuccess = { product ->
                callback(product)
            }) {}
        }
    }
}
