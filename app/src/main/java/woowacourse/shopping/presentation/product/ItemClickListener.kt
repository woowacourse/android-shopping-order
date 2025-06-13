package woowacourse.shopping.presentation.product

import woowacourse.shopping.presentation.uimodel.CartItemUiModel

interface ItemClickListener {
    fun onClickProductItem(productId: Long)

    fun onClickAddToCart(cartItemUiModel: CartItemUiModel)
}
