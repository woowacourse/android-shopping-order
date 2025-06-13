package woowacourse.shopping.feature.goods.adapter

import woowacourse.shopping.feature.goods.GoodsProduct

interface GoodsClickListener {
    fun onClickGoods(productId: Int)

    fun onClickHistory(productId: Int)

    fun addToCart(productId: Int)

    fun increaseQuantity(cart: GoodsProduct)

    fun decreaseQuantity(cart: GoodsProduct)

    fun loadMore()
}
