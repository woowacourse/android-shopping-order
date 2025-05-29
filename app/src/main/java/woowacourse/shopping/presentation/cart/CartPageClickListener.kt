package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.CartItemUiModel

interface CartPageClickListener {
    fun onClickDelete(cartItem: CartItemUiModel)

    fun onClickRecommend()
}
