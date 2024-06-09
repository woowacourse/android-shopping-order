package woowacourse.shopping.presentation.ui.cart

import kotlinx.coroutines.Job
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel

interface CartActionHandler : ProductCountHandler {
    fun onDelete(cartProductUiModel: CartProductUiModel): Job

    fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    )

    fun onCheckAll()
}
