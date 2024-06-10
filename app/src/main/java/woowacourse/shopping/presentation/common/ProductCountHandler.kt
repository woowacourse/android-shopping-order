package woowacourse.shopping.presentation.common

interface ProductCountHandler {
    fun plusProductQuantity(productId: Long)

    fun minusProductQuantity(productId: Long)
}
