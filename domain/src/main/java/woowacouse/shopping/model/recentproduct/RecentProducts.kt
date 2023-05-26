package woowacouse.shopping.model.recentproduct

class RecentProducts(
    recentProducts: List<RecentProduct>
) {
    private val recentProducts = recentProducts.toMutableList()

    fun addRecentProduct(recentProduct: RecentProduct) {
        recentProducts.add(recentProduct)
    }

    fun deleteRecentProduct(recentProductId: Long) {
        recentProducts.removeIf { it.id == recentProductId }
    }

    fun getRecentProduct(recentProductId: Long): RecentProduct? =
        recentProducts.find { it.id == recentProductId }

    fun getAll(): List<RecentProduct> = recentProducts.toList()
}
