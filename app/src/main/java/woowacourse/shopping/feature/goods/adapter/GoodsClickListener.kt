package woowacourse.shopping.feature.goods.adapter

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.History

interface GoodsClickListener {
    fun onClickGoods(cart: CartProduct)

    fun onClickHistory(history: History)

    fun insertToCart(cart: CartProduct)

    fun removeFromCart(cart: CartProduct)

    fun loadMore()
}
