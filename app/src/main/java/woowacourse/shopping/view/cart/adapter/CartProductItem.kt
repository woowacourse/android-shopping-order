package woowacourse.shopping.view.cart.adapter

import woowacourse.shopping.domain.model.CartProduct

data class CartProductItem(
    val cartProduct: CartProduct,
    val isSelected: Boolean = false,
)
