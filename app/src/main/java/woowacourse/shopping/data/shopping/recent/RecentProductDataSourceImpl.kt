package woowacourse.shopping.data.shopping.recent

import woowacourse.shopping.local.dao.RecentProductDao

class RecentProductDataSourceImpl(
    private val recentProductDao: RecentProductDao,
) : RecentProductDataSource {
    override suspend fun recentProducts(size: Int): Result<List<RecentProductData>> {
        return runCatching {
            recentProductDao.loadProducts(size).map { it.toData() }
        }
    }

    override suspend fun saveRecentProduct(product: RecentProductData): Result<Long> {
        return runCatching {
            recentProductDao.saveProduct(product.toEntity())
        }
    }
}
