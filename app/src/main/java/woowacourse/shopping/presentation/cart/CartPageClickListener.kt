package woowacourse.shopping.presentation.cart

import woowacourse.shopping.domain.model.CartItem

interface CartPageClickListener {
    fun onClickDelete(cartItem: CartItem)

    fun onClickRecommend()
}
