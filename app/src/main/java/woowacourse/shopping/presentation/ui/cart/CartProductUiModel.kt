package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.domain.CartProduct

data class CartProductUiModel(
    val cartProduct: CartProduct,
    var isChecked: Boolean = true,
)
