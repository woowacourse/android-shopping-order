package woowacourse.shopping.common

interface OnItemQuantityChangeListener {
    fun onIncrease(
        productId: Long,
        quantity: Int,
    )

    fun onDecrease(
        productId: Long,
        quantity: Int,
    )
}
