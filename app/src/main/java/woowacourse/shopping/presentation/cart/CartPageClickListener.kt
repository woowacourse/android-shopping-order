package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.model.CartItemUiModel

interface CartPageClickListener {
    fun onClickDelete(cartItem: CartItemUiModel)

    fun onClickSelect(cartId: Long)

    fun onClickCheckAll()

    fun onClickRecommend()
}
