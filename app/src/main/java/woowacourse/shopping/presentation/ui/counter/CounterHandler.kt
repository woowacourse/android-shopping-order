package woowacourse.shopping.presentation.ui.counter

interface CounterHandler {
    fun increaseCount(
        productId: Long,
        quantity: Int,
    )

    fun decreaseCount(
        productId: Long,
        quantity: Int,
    )
}
