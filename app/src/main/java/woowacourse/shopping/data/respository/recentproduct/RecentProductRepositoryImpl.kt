package woowacourse.shopping.data.respository.recentproduct

import woowacourse.shopping.data.model.RecentProductEntity
import woowacourse.shopping.data.respository.recentproduct.source.local.RecentProductLocalDataSource

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {

    override fun getRecentProducts(limit: Int): List<RecentProductEntity> {
        return recentProductLocalDataSource.getAllRecentProducts(limit)
    }

    override fun deleteNotTodayRecentProducts(today: String) {
        recentProductLocalDataSource.deleteNotToday(today)
    }

    override fun addCart(productId: Long) {
        recentProductLocalDataSource.insertRecentProduct(productId)
    }
}
