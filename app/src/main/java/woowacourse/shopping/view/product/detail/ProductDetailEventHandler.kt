package woowacourse.shopping.view.product.detail

import woowacourse.shopping.view.util.product.QuantityControlEventHandler

interface ProductDetailEventHandler : QuantityControlEventHandler {
    fun onAddToCartClick()

    fun onLastProductClick()
}
