package woowacourse.shopping.data.respository.recentproduct

import com.example.domain.RecentProduct

interface RecentProductRepository {
    fun getRecentProducts(limit: Int): List<RecentProduct>
    fun deleteNotTodayRecentProducts(today: String)
    fun addCart(productId: Long)
}
