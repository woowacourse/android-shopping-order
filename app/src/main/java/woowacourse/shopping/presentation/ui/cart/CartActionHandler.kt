package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.base.CartCountHandler

interface CartActionHandler : CartCountHandler {
    fun onDelete(cartProduct: CartProductUiModel)

    fun onNext()

    fun onPrevious()
}
