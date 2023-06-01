package woowacourse.shopping.data.dao.recentproduct

import woowacourse.shopping.data.entity.RecentProductEntity

interface RecentProductDao {
    fun getSize(): Int
    fun getRecentProducts(size: Int): List<RecentProductEntity>
    fun saveRecentProduct(item: RecentProductEntity)
    fun deleteLast()
}
