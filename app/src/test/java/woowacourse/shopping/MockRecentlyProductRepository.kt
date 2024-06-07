package woowacourse.shopping

import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository

class MockRecentlyProductRepository : RecentlyProductRepository {
    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct) {}

    override suspend fun getMostRecentlyProduct(): RecentlyProduct {
        return RecentlyProduct.defaultRecentlyProduct
    }

    override suspend fun getRecentlyProductList(): List<RecentlyProduct> {
        return listOf(RecentlyProduct.defaultRecentlyProduct)
    }

    override suspend fun deleteRecentlyProduct(id: Long) {}
}
