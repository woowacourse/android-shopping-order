package woowacourse.shopping.feature.cart.adapter

import woowacourse.shopping.domain.model.CartProduct

interface RecommendClickListener {
    fun insertToCart(cart: CartProduct)

    fun removeFromCart(cart: CartProduct)
}
