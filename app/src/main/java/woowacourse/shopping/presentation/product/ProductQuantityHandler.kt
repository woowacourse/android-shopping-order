package woowacourse.shopping.presentation.product

import woowacourse.shopping.product.catalog.ProductUiModel

interface ProductQuantityHandler {
    fun onPlusQuantity(product: ProductUiModel)

    fun onMinusQuantity(product: ProductUiModel)
}
