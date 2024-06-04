package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.presentation.base.CartCountHandler
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel

interface CartActionHandler : CartCountHandler {
    fun onDelete(cartProductUiModel: CartProductUiModel)

    fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    )

    fun onCheckAll()
}
