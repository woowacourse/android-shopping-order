package woowacourse.shopping.data.source

interface RecentlyViewedProductDataSource {
    fun insertRecentlyViewedProductUid(uid: Int)

    fun getRecentlyViewedProducts(callback: (List<Int>) -> Unit)

    fun getLatestViewedProduct(callback: (Int) -> Unit)
}
