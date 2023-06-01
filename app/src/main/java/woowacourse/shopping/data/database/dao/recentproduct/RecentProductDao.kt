package woowacourse.shopping.data.database.dao.recentproduct

import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.model.RecentProductEntity

interface RecentProductDao {
    fun getSize(): Int
    fun getPartially(size: Int): List<RecentProductEntity>
    fun add(product: ProductEntity)
    fun removeLast()
}
