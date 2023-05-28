package woowacourse.shopping.data.respository.recentproduct

import woowacourse.shopping.data.respository.recentproduct.source.local.RecentProductLocalDataSource
import woowacouse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacouse.shopping.model.recentproduct.RecentProducts

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {

    override fun getRecentProducts(limit: Int, onSuccess: (RecentProducts) -> Unit) {
        return recentProductLocalDataSource.getAllRecentProducts(limit, onSuccess)
    }

    override fun deleteNotTodayRecentProducts(today: String) {
        recentProductLocalDataSource.deleteNotToday(today)
    }

    override fun addCart(productId: Long) {
        recentProductLocalDataSource.insertRecentProduct(productId)
    }
}
