package woowacourse.shopping.domain.mapper

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.ui.model.CartModel

fun Cart.toUiModel(isChecked: Boolean = false) =
    CartModel(cartId, product.id, product.name, product.imgUrl, product.price, quantity, isChecked, calculatedPrice)
