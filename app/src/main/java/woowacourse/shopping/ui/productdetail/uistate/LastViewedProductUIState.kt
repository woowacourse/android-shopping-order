package woowacourse.shopping.ui.productdetail.uistate

import woowacourse.shopping.domain.RecentlyViewedProduct

data class LastViewedProductUIState(
    val id: Long,
    val name: String,
    val price: Int,
) {
    companion object {
        fun RecentlyViewedProduct.toUIState(): LastViewedProductUIState {
            return LastViewedProductUIState(
                id = product.id,
                name = product.name,
                price = product.price,
            )
        }
    }
}
