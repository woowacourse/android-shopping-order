package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.dao.RecentlyProductDao
import woowacourse.shopping.data.entity.RecentlyViewedProduct

class RecentProductLocalDataSourceImpl(
    private val dao: RecentlyProductDao,
) : RecentProductLocalDataSource {
    override suspend fun getProducts(): List<RecentlyViewedProduct> = dao.getProducts()

    override suspend fun getMostRecentProduct(): RecentlyViewedProduct? = dao.getMostRecentProduct()

    override suspend fun getOldestProduct(): RecentlyViewedProduct = dao.getOldestProduct()

    override suspend fun getCount(): Int = dao.getCount()

    override suspend fun insert(product: RecentlyViewedProduct) = dao.insertProduct(product)

    override suspend fun delete(product: RecentlyViewedProduct) = dao.delete(product)
}
