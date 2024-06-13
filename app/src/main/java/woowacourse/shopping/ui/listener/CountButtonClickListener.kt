package woowacourse.shopping.ui.listener

interface CountButtonClickListener {
    fun plusCount(productId: Long)

    fun minusCount(productId: Long)
}
