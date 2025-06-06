package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.product.catalog.ProductUiModel

class RecentlyViewedProductRepositoryImpl(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
    private val catalogProductRepository: CatalogProductRepository,
) : RecentlyViewedProductRepository {
    override suspend fun insertRecentlyViewedProductId(productId: Long) {
        recentlyViewedProductDao.insertRecentlyViewedProductId(
            RecentlyViewedProductEntity(productId),
        )
    }

    override suspend fun getRecentlyViewedProducts(): List<CartProductEntity> {
        val productIds: List<Long> = recentlyViewedProductDao.getRecentlyViewedProductIds()
        val products: List<ProductUiModel> =
            catalogProductRepository.getCartProductsByIds(productIds)
        val entities: List<CartProductEntity> = products.map { it.toEntity() }
        return entities
    }

    override suspend fun getLatestViewedProduct(): ProductUiModel? {
        val productId: Long = recentlyViewedProductDao.getLatestViewedProductId()
        val product: ProductUiModel? = catalogProductRepository.getProduct(productId)
        return product
    }
}
