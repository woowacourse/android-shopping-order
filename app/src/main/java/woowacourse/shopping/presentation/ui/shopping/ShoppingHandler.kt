package woowacourse.shopping.presentation.ui.shopping

import woowacourse.shopping.presentation.ui.QuantityHandler

interface ShoppingHandler : QuantityHandler {
    fun navigateToDetail(productId: Long)

    fun onLoadMoreProducts()

    fun navigateToCart()

    fun addProductToCart(productId: Long)
}
