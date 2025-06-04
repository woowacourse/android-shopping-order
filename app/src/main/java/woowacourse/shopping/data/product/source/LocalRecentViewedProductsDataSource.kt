package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.dao.RecentViewedProductDao
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity

class LocalRecentViewedProductsDataSource(
    private val dao: RecentViewedProductDao,
) : RecentViewedProductsDataSource {
    override fun load(): List<RecentViewedProductEntity> = dao.loadProducts()

    override fun upsert(product: RecentViewedProductEntity) {
        val count = dao.productsSize()
        if (count < MAX_ENTITY_COUNT) {
            dao.upsertProduct(product)
            return
        }

        dao.deleteProduct(count - MAX_ENTITY_COUNT)
        dao.upsertProduct(product)
    }

    companion object {
        private const val MAX_ENTITY_COUNT = 10
    }
}
