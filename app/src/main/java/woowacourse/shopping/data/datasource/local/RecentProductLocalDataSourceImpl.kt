package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.dao.RecentlyProductDao
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.data.handleResult

class RecentProductLocalDataSourceImpl(
    private val dao: RecentlyProductDao,
) : RecentProductLocalDataSource {
    override suspend fun getProducts(): Result<List<RecentlyViewedProduct>> = handleResult { dao.getProducts() }

    override suspend fun getMostRecentProduct(): Result<RecentlyViewedProduct?> = handleResult { dao.getMostRecentProduct() }

    override suspend fun getOldestProduct(): Result<RecentlyViewedProduct> = handleResult { dao.getOldestProduct() }

    override suspend fun getCount(): Result<Int> = handleResult { dao.getCount() }

    override suspend fun insert(product: RecentlyViewedProduct): Result<Unit> = handleResult { dao.insertProduct(product) }

    override suspend fun delete(product: RecentlyViewedProduct): Result<Unit> = handleResult { dao.delete(product) }
}
