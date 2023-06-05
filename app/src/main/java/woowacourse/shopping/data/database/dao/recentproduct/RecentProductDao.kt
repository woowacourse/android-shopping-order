package woowacourse.shopping.data.database.dao.recentproduct

import woowacourse.shopping.data.datasource.response.ProductEntity
import woowacourse.shopping.data.datasource.response.RecentProductEntity

interface RecentProductDao {
    fun getSize(): Int
    fun getPartially(size: Int): List<RecentProductEntity>
    fun add(product: ProductEntity)
    fun removeLast()
}
