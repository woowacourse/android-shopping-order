package woowacourse.shopping.data.shopping.recent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import woowacourse.shopping.local.dao.RecentProductDao

class RecentProductDataSourceImpl(
    private val recentProductDao: RecentProductDao,
) : RecentProductDataSource {
    override suspend fun recentProducts(size: Int): Result<List<RecentProductData>> {
        return runCatching {
            val recentProductsDeferred =
                CoroutineScope(Dispatchers.IO).async {
                    recentProductDao.loadProducts(size)
                }
            recentProductsDeferred.await().map { it.toData() }
        }
    }

    override suspend fun saveRecentProduct(product: RecentProductData): Result<Long> {
        return runCatching {
            val idDeferred =
                CoroutineScope(Dispatchers.IO).async {
                    recentProductDao.saveProduct(product.toEntity())
                }
            idDeferred.await()
        }
    }
}
