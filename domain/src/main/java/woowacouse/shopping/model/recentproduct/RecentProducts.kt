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

    fun getRecentProduct(recentProductId: Long): RecentProduct =
        recentProducts.find { it.id == recentProductId } ?: throw IllegalArgumentException(
            ERROR_NOT_EXITS_RECENT_ID
        )

    fun getAll(): List<RecentProduct> = recentProducts.toList()

    companion object {
        private const val ERROR_NOT_EXITS_RECENT_ID = "존재하지 않는 최근 본 상품 ID입니다"
    }
}
