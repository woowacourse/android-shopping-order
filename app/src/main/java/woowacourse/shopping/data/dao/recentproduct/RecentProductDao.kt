package woowacourse.shopping.data.dao.recentproduct

import woowacourse.shopping.data.entity.RecentProductEntity

interface RecentProductDao {
    fun getSize(): Int
    fun getRecentProductsPartially(size: Int): List<RecentProductEntity>
    fun addRecentProduct(item: RecentProductEntity)
    fun removeLast()
}
