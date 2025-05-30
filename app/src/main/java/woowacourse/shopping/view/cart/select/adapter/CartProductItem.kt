package woowacourse.shopping.view.cart.select.adapter

import woowacourse.shopping.domain.model.CartProduct

data class CartProductItem(
    val cartProduct: CartProduct,
    val isSelected: Boolean = false,
)
