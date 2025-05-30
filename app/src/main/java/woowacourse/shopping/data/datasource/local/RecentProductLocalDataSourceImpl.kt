package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.dao.RecentlyProductDao
import woowacourse.shopping.data.entity.RecentlyViewedProduct

class RecentProductLocalDataSourceImpl(
    private val dao: RecentlyProductDao,
) : RecentProductLocalDataSource {
    override fun getProducts(): List<RecentlyViewedProduct> = dao.getProducts()

    override fun getMostRecentProduct(): RecentlyViewedProduct? = dao.getMostRecentProduct()

    override fun getOldestProduct(): RecentlyViewedProduct = dao.getOldestProduct()

    override fun getCount(): Int = dao.getCount()

    override fun insert(product: RecentlyViewedProduct) = dao.insertProduct(product)

    override fun delete(product: RecentlyViewedProduct) = dao.delete(product)
}
