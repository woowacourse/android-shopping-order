package woowacourse.shopping.ui.productlist.uistate

import woowacourse.shopping.domain.RecentlyViewedProduct

data class RecentlyViewedProductUIState(
    val productId: Long,
    val imageUrl: String,
    val name: String,
    val recentlyViewedProductId: Long
) {
    companion object {
        fun RecentlyViewedProduct.toUIState(): RecentlyViewedProductUIState {
            return RecentlyViewedProductUIState(
                productId = product.id,
                imageUrl = product.imageUrl,
                name = product.name,
                recentlyViewedProductId = id,
            )
        }
    }
}
