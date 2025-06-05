package woowacourse.shopping.view.product.detail

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.view.util.product.QuantityControlEventHandler

interface ProductDetailEventHandler : QuantityControlEventHandler<Product> {
    fun onAddToCartClick()

    fun onLastViewedProductClick()
}
