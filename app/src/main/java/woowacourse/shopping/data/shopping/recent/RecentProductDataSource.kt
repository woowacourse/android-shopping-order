package woowacourse.shopping.data.shopping.recent

interface RecentProductDataSource {
    suspend fun recentProducts(size: Int): Result<List<RecentProductData>>

    suspend fun saveRecentProduct(product: RecentProductData): Result<Long>
}
