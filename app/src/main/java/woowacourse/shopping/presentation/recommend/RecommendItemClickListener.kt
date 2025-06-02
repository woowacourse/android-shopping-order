package woowacourse.shopping.presentation.recommend

import woowacourse.shopping.presentation.model.CartItemUiModel

interface RecommendItemClickListener {
    fun onClickAddToCart(cartItem: CartItemUiModel)
}
