package woowacourse.shopping.data.respository.recentproduct.source.local

import woowacourse.shopping.data.model.RecentProductEntity

interface RecentProductLocalDataSource {
    fun insertRecentProduct(productId: Long)
    fun deleteNotToday(today: String)
    fun getAllRecentProducts(limit: Int): List<RecentProductEntity>
}
