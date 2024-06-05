package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.presentation.base.CartCountHandler

interface CartActionHandler : CartCountHandler {
    fun onDelete(cartProductUiModel: CartProductUiModel)

    fun onNext()

    fun onPrevious()

    fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    )

    fun onCheckAll()

    fun onOrderClick(orderItems: List<CartProductUiModel>)
}
