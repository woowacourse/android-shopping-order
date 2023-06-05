package woowacouse.shopping.data.repository.recentproduct

import woowacouse.shopping.model.recentproduct.RecentProducts

interface RecentProductRepository {
    fun getRecentProducts(
        limit: Int,
        onFailure: (message: String) -> Unit,
        onSuccess: (RecentProducts) -> Unit
    )
    fun deleteNotTodayRecentProducts(today: String)
    fun addCart(productId: Long)
}