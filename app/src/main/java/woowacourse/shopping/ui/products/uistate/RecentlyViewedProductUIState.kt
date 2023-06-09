package woowacourse.shopping.ui.products.uistate

import woowacourse.shopping.domain.RecentlyViewedProduct

data class RecentlyViewedProductUIState(
    val imageUrl: String,
    val name: String,
    val recentlyViewedProductId: Long,
    val productId: Long
) {
    companion object {
        fun from(recentlyViewedProduct: RecentlyViewedProduct): RecentlyViewedProductUIState {
            val product = recentlyViewedProduct.product
            val recentlyViewedProductId = recentlyViewedProduct.id
            return RecentlyViewedProductUIState(
                product.imageUrl,
                product.name,
                recentlyViewedProductId,
                product.id
            )
        }
    }
}
