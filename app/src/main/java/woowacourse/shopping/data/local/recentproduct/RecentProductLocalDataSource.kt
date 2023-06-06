package woowacourse.shopping.data.local.recentproduct

interface RecentProductLocalDataSource {
    fun getRecentProductIdList(size: Int): List<Int>
    fun getMostRecentProductId(): Int
    fun addRecentProduct(recentProductId: Int)
    fun deleteAllProduct()
    fun deleteRecentProduct(recentProductId: Int)
}
