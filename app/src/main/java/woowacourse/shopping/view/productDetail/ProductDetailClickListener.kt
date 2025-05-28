package woowacourse.shopping.view.productDetail

import woowacourse.shopping.view.common.ProductQuantityClickListener

interface ProductDetailClickListener : ProductQuantityClickListener {
    fun onCloseButton()

    fun onAddingToShoppingCart()
}
