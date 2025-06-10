package woowacourse.shopping.presentation.recommend

import woowacourse.shopping.presentation.model.CartItemUiModel

interface RecommendClickListener {
    fun onClickAddToCart(cartItem: CartItemUiModel)

    fun onClickOrder()
}
