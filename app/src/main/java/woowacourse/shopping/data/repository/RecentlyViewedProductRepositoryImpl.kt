package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.product.catalog.ProductUiModel

class RecentlyViewedProductRepositoryImpl(
    val recentlyViewedProductDao: RecentlyViewedProductDao,
    val catalogProductRepository: CatalogProductRepository,
) : RecentlyViewedProductRepository {
    override suspend fun insertRecentlyViewedProductUid(uid: Int) {
        recentlyViewedProductDao.insertRecentlyViewedProductUid(RecentlyViewedProductEntity(uid))
    }

    override suspend fun getRecentlyViewedProducts(): List<CartProductEntity> {
        val uids = recentlyViewedProductDao.getRecentlyViewedProductUids()
        val products = catalogProductRepository.getCartProductsByUids(uids)
        val entities = products.map { it.toEntity() }
        return entities
    }

    override suspend fun getLatestViewedProduct(): ProductUiModel? {
        val uid = recentlyViewedProductDao.getLatestViewedProductUid()
        val product = catalogProductRepository.getProduct(uid)
        return product
    }
}
