package woowacourse.shopping.presentation.common

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.common.ProductCountHandler

interface ProductClickHandler : ProductCountHandler {
    fun onProductClick(cartProduct: CartProduct)
}
