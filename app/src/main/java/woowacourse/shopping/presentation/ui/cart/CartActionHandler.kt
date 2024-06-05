package woowacourse.shopping.presentation.ui.cart

import kotlinx.coroutines.Job
import woowacourse.shopping.presentation.base.CartCountHandler
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel

interface CartActionHandler : CartCountHandler {
    fun onDelete(cartProductUiModel: CartProductUiModel): Job

    fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    )

    fun onCheckAll()
}
