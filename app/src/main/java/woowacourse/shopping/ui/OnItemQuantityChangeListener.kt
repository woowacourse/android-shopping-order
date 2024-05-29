package woowacourse.shopping.ui

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
