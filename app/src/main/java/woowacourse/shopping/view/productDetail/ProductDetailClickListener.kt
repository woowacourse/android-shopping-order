package woowacourse.shopping.view.productDetail

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.common.ProductQuantityClickListener

interface ProductDetailClickListener : ProductQuantityClickListener {
    fun onCloseButton()

    fun onAddingToShoppingCart()

    fun onRecentProductClick(product: Product)
}
