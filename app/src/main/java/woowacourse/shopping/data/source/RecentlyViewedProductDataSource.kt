package woowacourse.shopping.data.source

interface RecentlyViewedProductDataSource {
    suspend fun insertRecentlyViewedProductUid(uid: Int)

    suspend fun getRecentlyViewedProducts(): List<Int>

    suspend fun getLatestViewedProduct(): Int
}
