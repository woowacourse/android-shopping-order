package woowacourse.shopping.view.cart

interface QuantityEventListener {
    fun addQuantity(cartItemId: Int)

    fun subtractQuantity(cartItemId: Int)
}
