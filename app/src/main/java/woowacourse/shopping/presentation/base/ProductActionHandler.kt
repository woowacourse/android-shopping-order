package woowacourse.shopping.presentation.base

import woowacourse.shopping.domain.CartProduct

interface ProductActionHandler: CartCountHandler {
    fun onProductClick(cartProduct: CartProduct)
}