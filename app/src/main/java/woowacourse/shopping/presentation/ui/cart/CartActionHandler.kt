package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.presentation.base.CartCountHandler

interface CartActionHandler : CartCountHandler {
    fun onDelete(cartProduct: CartProductUiModel)

    fun onNext()

    fun onPrevious()

    fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    )

    fun onCheckAll()
}
