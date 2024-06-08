package woowacourse.shopping.domain.mapper

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.model.CartUiModel

fun Cart.toPresentation(): CartUiModel {
    return CartUiModel(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toPresentation(),
    )
}
