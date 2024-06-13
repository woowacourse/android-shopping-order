package woowacourse.shopping.presentation.ui.detail

import woowacourse.shopping.presentation.ui.QuantityHandler

interface DetailHandler : QuantityHandler {
    fun addProductToCart()

    fun navigateToDetailWithRecentViewed(productId: Long)

    fun navigateToBack()
}
