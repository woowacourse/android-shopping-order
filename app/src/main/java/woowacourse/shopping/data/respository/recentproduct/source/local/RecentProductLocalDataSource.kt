package woowacourse.shopping.data.respository.recentproduct.source.local

import woowacouse.shopping.model.recentproduct.RecentProducts

interface RecentProductLocalDataSource {
    fun insertRecentProduct(productId: Long)
    fun deleteNotToday(today: String)
    fun getAllRecentProducts(limit: Int, onSuccess: (RecentProducts) -> Unit)
}
