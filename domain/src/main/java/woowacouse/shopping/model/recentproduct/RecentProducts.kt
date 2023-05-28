package woowacouse.shopping.model.recentproduct

class RecentProducts(
    private val recentProducts: List<RecentProduct>
) {
    fun addRecentProduct(recentProduct: RecentProduct): RecentProducts {
        val newRecentProducts = recentProducts.toMutableList()
        newRecentProducts.add(recentProduct)
        return RecentProducts(newRecentProducts.toList())
    }

    fun deleteRecentProduct(recentProductId: Long): RecentProducts {
        return RecentProducts(recentProducts.filterNot { it.id == recentProductId })
    }

    fun getRecentProduct(recentProductId: Long): RecentProduct? =
        recentProducts.find { it.id == recentProductId }

    fun getAll(): List<RecentProduct> = recentProducts.toList()
}
