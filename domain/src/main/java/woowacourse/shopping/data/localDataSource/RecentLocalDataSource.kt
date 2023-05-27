package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct

interface RecentLocalDataSource {
    fun insert(product: Product)
    fun getRecent(maxSize: Int): List<RecentProduct>
    fun delete(id: Int)
    fun findById(id: Int): RecentProduct?
}
