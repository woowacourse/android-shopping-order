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
    override suspend fun insertRecentlyViewedProductId(productId: Long): Result<Unit> =
        runCatching {
            recentlyViewedProductDao.insertRecentlyViewedProductId(
                RecentlyViewedProductEntity(productId),
            )
        }

    override suspend fun getRecentlyViewedProducts(): Result<List<CartProductEntity>> =
        runCatching {
            val productIds: List<Long> = recentlyViewedProductDao.getRecentlyViewedProductIds()
            if (productIds.isEmpty()) return@runCatching emptyList<CartProductEntity>()
            val productsResult = catalogProductRepository.getCartProductsByIds(productIds)
            productsResult
                .getOrThrow()
                .map { it.toEntity() }
        }

    override suspend fun getLatestViewedProduct(): Result<ProductUiModel?> =
        runCatching {
            val productId = recentlyViewedProductDao.getLatestViewedProductId()
            val productResult = catalogProductRepository.getProduct(productId)
            productResult.getOrNull()
        }
}
