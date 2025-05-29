package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.CartItemUiModel

interface CartPageClickListener {
    fun onClickDelete(cartItem: CartItemUiModel)

    fun onClickSelect(cartId: Long)

    fun onClickRecommend()
}
