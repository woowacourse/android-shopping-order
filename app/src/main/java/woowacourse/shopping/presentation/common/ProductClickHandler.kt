package woowacourse.shopping.presentation.common

import woowacourse.shopping.domain.CartProduct

interface ProductClickHandler : ProductCountHandler {
    fun onProductClick(cartProduct: CartProduct)
}
