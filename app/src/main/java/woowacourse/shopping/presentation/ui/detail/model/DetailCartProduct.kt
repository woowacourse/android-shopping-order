package woowacourse.shopping.presentation.ui.detail.model

import woowacourse.shopping.domain.CartProduct

data class DetailCartProduct(
    val isNew: Boolean = false,
    val cartProduct: CartProduct,
) {
    companion object {
        fun fromCartProduct(cartProduct: CartProduct): DetailCartProduct {
            return DetailCartProduct(
                isNew = cartProduct.quantity == 0,
                cartProduct =
                    cartProduct.copy(
                        quantity = if (cartProduct.quantity == 0) 1 else cartProduct.quantity,
                    ),
            )
        }
    }
}
