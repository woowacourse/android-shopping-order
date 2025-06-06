package woowacourse.shopping.cart

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartActionListener {
    fun onDeleteProduct(product: ProductUiModel)

    fun onCheckToggle(product: ProductUiModel)
}
