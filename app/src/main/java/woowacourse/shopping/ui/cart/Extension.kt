package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.model.cart.CartWithProduct

fun CartWithProduct.toUiModel(isChecked: Boolean) =
    CartUiModel(
        this.id,
        this.product.id,
        this.product.name,
        this.product.price,
        this.quantity,
        this.product.imageUrl,
        isChecked,
    )
