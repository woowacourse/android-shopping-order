package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.uimodel.CartItemUiModel

interface CartPageClickListener {
    fun onClickDelete(cartItem: CartItemUiModel)

    fun onClickSelect(cartId: Long)

    fun onClickCheckAll()

    fun onClickRecommend()
}
