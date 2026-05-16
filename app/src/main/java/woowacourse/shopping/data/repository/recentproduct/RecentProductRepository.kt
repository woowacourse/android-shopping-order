package woowacourse.shopping.data.repository.recentproduct

interface RecentProductRepository {
    suspend fun loadProducts(): List<Long>

    suspend fun insert(id: Long)
}
