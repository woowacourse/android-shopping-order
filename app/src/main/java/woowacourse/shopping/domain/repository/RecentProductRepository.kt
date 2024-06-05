package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct

interface RecentProductRepository {
    fun insert(productId: Long): Long

    fun findMostRecentProduct(): RecentProduct?

    fun findAll(): List<RecentProduct>

    fun deleteAll()
}
