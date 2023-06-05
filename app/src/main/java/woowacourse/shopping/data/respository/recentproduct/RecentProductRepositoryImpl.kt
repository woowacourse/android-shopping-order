package woowacourse.shopping.data.respository.recentproduct

import com.example.domain.RecentProduct
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.respository.recentproduct.source.local.RecentProductLocalDataSource

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {

    override fun getRecentProducts(limit: Int): List<RecentProduct> {
        val recentProducts = recentProductLocalDataSource.getAllRecentProducts(limit)
        return recentProducts.map { it.toDomain() }
    }

    override fun deleteNotTodayRecentProducts(today: String) {
        recentProductLocalDataSource.deleteNotToday(today)
    }

    override fun addCart(productId: Long) {
        recentProductLocalDataSource.insertRecentProduct(productId)
    }
}
