package woowacourse.shopping.data.recentproduct

interface RecentProductRepository {
    fun insert(productId: Long): Result<Long>

    fun findMostRecentProduct(): Result<RecentProduct>

    fun findAll(): Result<List<RecentProduct>>

    fun deleteAll(): Result<Unit>
}
