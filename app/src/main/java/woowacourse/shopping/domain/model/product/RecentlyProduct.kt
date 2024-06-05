package woowacourse.shopping.domain.model.product

data class RecentlyProduct(
    val id: Long = DEFAULT_RECENTLY_PRODUCT_ID,
    val productId: Long,
    val imageUrl: String,
    val name: String,
    val category: String,
) {
    companion object {
        const val DEFAULT_RECENTLY_PRODUCT_ID = -1L
        val defaultRecentlyProduct =
            RecentlyProduct(
                DEFAULT_RECENTLY_PRODUCT_ID,
                DEFAULT_RECENTLY_PRODUCT_ID,
                "",
                "",
                "",
            )
    }
}
