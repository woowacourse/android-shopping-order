package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProducts

interface RecentProductRepository {
    fun getAll(onSuccess: (RecentProducts) -> Unit, onFailure: (String) -> Unit)

    fun addRecentProduct(recentProduct: RecentProduct)

    fun updateRecentProduct(recentProduct: RecentProduct)

    fun getLatestRecentProduct(onSuccess: (RecentProduct?) -> Unit, onFailure: (String) -> Unit)

    fun isExist(id: Int): Boolean
}
