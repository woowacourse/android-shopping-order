package woowacourse.shopping.view.core.handler

interface CartQuantityHandler {
    fun onClickIncrease(cartId: Long)

    fun onClickDecrease(cartId: Long)
}
